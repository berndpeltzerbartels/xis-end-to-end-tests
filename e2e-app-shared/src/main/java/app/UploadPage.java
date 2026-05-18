package app;

import one.xis.Action;
import one.xis.FormData;
import one.xis.ModelData;
import one.xis.Page;
import one.xis.Upload;
import one.xis.UploadedFile;

@Page("/upload.html")
public class UploadPage {

    private String result = "";

    @FormData("upload")
    UploadForm upload() {
        return new UploadForm();
    }

    @ModelData("result")
    String result() {
        return result;
    }

    @Action("save")
    void save(@FormData("upload") UploadForm form) {
        result = form.getTitle() + ":" + form.getAttachment().getFileName() + ":" + form.getAttachment().getUtf8Text();
    }

    public static class UploadForm {
        private String title;

        @Upload(maxSize = 64)
        private UploadedFile attachment;

        public String getTitle() {
            return title;
        }

        public UploadedFile getAttachment() {
            return attachment;
        }
    }
}
