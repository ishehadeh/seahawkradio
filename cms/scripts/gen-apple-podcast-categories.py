

from datetime import datetime
import enum
from html.parser import HTMLParser
from html import unescape
import sys
import urllib.request

class CategoryExtractor(HTMLParser):
    class State(enum.Enum):
        NONE = 0
        MAIN = 1
        TABLE = 2
        TABLE_HEADER = 3
        TABLE_ROW = 4
        TABLE_CELL = 8
        TABLE_CELL_CONTENT = 5
        SUBCAT_LIST = 6
        SUBCAT_LIST_ITEM = 7

    def __init__(self):
        super().__init__()

        # stack of states
        self._state = [CategoryExtractor.State.NONE]
        self._found_header = False
        self._category = None
        self._subcategories = []
        self.categories = []

    def _push_state(self, new_state):
        self._state.append(new_state)

    def _pop_state(self):
        self._state.pop()

    def state(self):
        return self._state[-1]

    def _try_push_category(self):
        if self._category is not None:
            # table contains HTML escaped names and unescaped names.
            # check to make sure the category hasn't already been entered to avoid duplicates
            if len(self.categories) == 0 or self.categories[-1][0] != unescape(self._category):
                self.categories.append((self._category, self._subcategories))

            self._subcategories = []
            self._category = None

    def handle_starttag(self, tag, _attrs):
        if self.state() == CategoryExtractor.State.NONE:
            if tag == "main":
                self._push_state(CategoryExtractor.State.MAIN)
        elif self.state() == CategoryExtractor.State.MAIN:
            if tag == "table":
                self._push_state(CategoryExtractor.State.TABLE)
        elif self.state() == CategoryExtractor.State.TABLE:
            if tag == "tr":
                if self._found_header:
                    self._push_state(CategoryExtractor.State.TABLE_ROW)
                else:
                    self._push_state(CategoryExtractor.State.TABLE_HEADER)
        elif self.state() == CategoryExtractor.State.TABLE_ROW:
            if tag == "td":
                self._push_state(CategoryExtractor.State.TABLE_CELL)
        elif self.state() == CategoryExtractor.State.TABLE_CELL:
            if tag == "p":
                self._push_state(CategoryExtractor.State.TABLE_CELL_CONTENT)
            elif tag == "ul":
                self._push_state(CategoryExtractor.State.SUBCAT_LIST)
        elif self.state() == CategoryExtractor.State.SUBCAT_LIST:
            if tag == "li":
                self._push_state(CategoryExtractor.State.SUBCAT_LIST_ITEM)

    def handle_endtag(self, tag):
        if self.state() == CategoryExtractor.State.MAIN:
            if tag == "main":
                self._pop_state()
                self._try_push_category()
        elif self.state() == CategoryExtractor.State.TABLE:
            if tag == "table":
                self._pop_state()
        elif self.state() in [CategoryExtractor.State.TABLE_ROW, CategoryExtractor.State.TABLE_HEADER]:
            if tag == "tr":
                self._found_header = True
                self._pop_state()
        elif self.state() == CategoryExtractor.State.TABLE_CELL:
            if tag == "td":
                self._pop_state()
        elif self.state() in [CategoryExtractor.State.TABLE_CELL_CONTENT, CategoryExtractor.State.SUBCAT_LIST_ITEM]:
            self._pop_state()
        elif self.state() == CategoryExtractor.State.SUBCAT_LIST:
            if tag == "ul":
                self._pop_state()

    def handle_data(self, data):
        if self.state() == CategoryExtractor.State.SUBCAT_LIST_ITEM:
            self._subcategories.append(data.strip())
        elif self.state() == CategoryExtractor.State.TABLE_CELL_CONTENT:
            self._try_push_category()
            self._category = data.strip()

req = urllib.request.Request(
    "https://podcasters.apple.com/support/1691-apple-podcasts-categories",
    headers={
        "User-Agent": "Mozilla/5.0 (X11; Linux x86_64; rv:104.0) Gecko/20100101 Firefox/104.0",
        "Accept": "text/html",
    })
FILE_HEADER = f"""package seahawkradio.cms;
// DO NOT EDIT
// This file was generated by '{sys.argv[0]}' on {datetime.now().isoformat()}

import java.util.Set;

public record ApplePodcastCategory(String name, Set<String> subcategories) {{
    public static final ApplePodcastCategory[] ALL = new ApplePodcastCategory[] {{"""

FILE_FOOTER="    };\n}\n"

with urllib.request.urlopen(req) as f:
    page = f.read().decode("utf-8")
    catex = CategoryExtractor()
    catex.feed(page)
    catex.close()
    print(FILE_HEADER)
    for category, subcategories in catex.categories:
        if len(subcategories) > 0:
            subcategory_strings =  '"' + '", "'.join(subcategories) + '"'
        else:
            subcategory_strings = ""
        print(f"         new ApplePodcastCategory(\"{category}\", Set.of({subcategory_strings})),")
    print(FILE_FOOTER)
