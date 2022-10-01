import ClassicEditor from '@ckeditor/ckeditor5-editor-classic/src/classiceditor';
import Essentials from '@ckeditor/ckeditor5-essentials/src/essentials';
import Paragraph from '@ckeditor/ckeditor5-paragraph/src/paragraph';
import { Bold, Italic, Strikethrough, Underline, Subscript } from '@ckeditor/ckeditor5-basic-styles/';
import SimpleUploadAdapter from '@ckeditor/ckeditor5-upload/src/adapters/simpleuploadadapter';
import ImageUpload from '@ckeditor/ckeditor5-image/src/imageupload';
import PasteFromOffice from '@ckeditor/ckeditor5-paste-from-office/src/pastefromoffice';
import { Image, ImageResize, ImageResizeHandles, ImageStyle } from '@ckeditor/ckeditor5-image';
import GeneralHtmlSupport from '@ckeditor/ckeditor5-html-support/src/generalhtmlsupport';
import SourceEditing from '@ckeditor/ckeditor5-source-editing/src/sourceediting';
import Heading from '@ckeditor/ckeditor5-heading/src/heading';
import Alignment from '@ckeditor/ckeditor5-alignment/src/alignment';
import { FontFamily, FontSize, FontColor, FontBackgroundColor } from '@ckeditor/ckeditor5-font';
import Link from '@ckeditor/ckeditor5-link/src/link';
import ListProperties from '@ckeditor/ckeditor5-list/src/listproperties';

export function initEditor(elem) {
    ClassicEditor
        .create(elem, {
            plugins: [Heading, Essentials, Paragraph, ListProperties, Link, Bold, Italic, Strikethrough, Underline, Subscript, FontFamily, FontSize, FontColor, FontBackgroundColor, Image, ImageResize, ImageResizeHandles, ImageStyle, ImageUpload, Alignment, PasteFromOffice, SimpleUploadAdapter, GeneralHtmlSupport, SourceEditing],
            toolbar: {
                items: [
                    'heading', '|',
                    'fontfamily', 'fontsize', 'alignment', 'fontColor', 'fontBackgroundColor', '|',
                    'bold', 'italic', 'strikethrough', 'underline', 'subscript', 'superscript', '|',
                    'outdent', 'indent', '|',
                    'bulletedList', 'numberedList', '|',
                    'link', 'uploadImage', '|',
                    'undo', 'redo', '|',
                    'sourceEditing',
                ],
                shouldNotGroupWhenFull: true
            },
            heading: {
                options: [
                    { model: 'paragraph', title: 'Paragraph', class: 'ck-heading_paragraph' },
                    { model: 'heading1', view: 'h1', title: 'Heading 1', class: 'ck-heading_heading1' },
                    { model: 'heading2', view: 'h2', title: 'Heading 2', class: 'ck-heading_heading2' }
                ]
            },
            simpleUpload: {
                uploadUrl: 'http://localhost:8080/api/image',
            }
        })
        .catch(error => {
            console.error(error);
        });

}

// Init an editor for podcast descriptions
// it only supports the most commen set of allowed elements by podcast players.
// See:
//    - https://support.spotifyforpodcasters.com/hc/en-us/articles/360044283811-HTML-formatting-in-podcast-descriptions
export function makePodcastEditor(elem) {
    ClassicEditor
        .create(elem, {
            plugins: [Heading, Essentials, Paragraph, ListProperties, Link, Bold, Italic, Image, ImageResize, ImageResizeHandles, ImageUpload, PasteFromOffice, SimpleUploadAdapter, SourceEditing],
            toolbar: {
                items: [
                    'heading', '|',
                    'bold', 'italic', '|',
                    'outdent', 'indent', '|',
                    'bulletedList', 'numberedList', '|',
                    'link', 'uploadImage', '|',
                    'undo', 'redo', '|',
                    'sourceEditing',
                ],
                shouldNotGroupWhenFull: true
            },
            heading: {
                options: [
                    { model: 'paragraph', title: 'Paragraph' },
                    { model: 'heading1', view: 'h1', title: 'Heading 1' },
                    { model: 'heading2', view: 'h2', title: 'Heading 2' },
                    { model: 'heading3', view: 'h3', title: 'Heading 3' }
                ]
            },
            simpleUpload: {
                uploadUrl: 'http://localhost:8080/api/image',
            }
        })
        .catch(error => {
            console.error(error);
        });
}
