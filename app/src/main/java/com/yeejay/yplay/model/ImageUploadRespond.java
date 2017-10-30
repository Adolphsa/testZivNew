package com.yeejay.yplay.model;

/**
 * 图片上传返回
 * Created by Administrator on 2017/10/30.
 */

public class ImageUploadRespond {


    /**
     * code : 0
     * message : SUCCESS
     * request_id : NTlhNDBhZGVfOTYyMjViNjRfMTc3Ml8yYWQ5NWU=
     * data : {"access_url":"http://iainyu-1251668577.file.myqcloud.com/sample_file.txt","resource_path":"/1251668577/iainyu/sample_file.txt","source_url":"http://iainyu-1251668577.cosgz.myqcloud.com/sample_file.txt","url":"http://gz.file.myqcloud.com/files/v2/1251668577/iainyu/sample_file.txt","vid":"dce2a8d7ba11d045c0e19019fab807911503922910"}
     */

    private int code;
    private String message;
    private String request_id;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ImageUploadRespond{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", request_id='" + request_id + '\'' +
                ", data=" + data +
                '}';
    }

    public static class DataBean {
        /**
         * access_url : http://iainyu-1251668577.file.myqcloud.com/sample_file.txt
         * resource_path : /1251668577/iainyu/sample_file.txt
         * source_url : http://iainyu-1251668577.cosgz.myqcloud.com/sample_file.txt
         * url : http://gz.file.myqcloud.com/files/v2/1251668577/iainyu/sample_file.txt
         * vid : dce2a8d7ba11d045c0e19019fab807911503922910
         */

        private String access_url;
        private String resource_path;
        private String source_url;
        private String url;
        private String vid;

        public String getAccess_url() {
            return access_url;
        }

        public void setAccess_url(String access_url) {
            this.access_url = access_url;
        }

        public String getResource_path() {
            return resource_path;
        }

        public void setResource_path(String resource_path) {
            this.resource_path = resource_path;
        }

        public String getSource_url() {
            return source_url;
        }

        public void setSource_url(String source_url) {
            this.source_url = source_url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getVid() {
            return vid;
        }

        public void setVid(String vid) {
            this.vid = vid;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "access_url='" + access_url + '\'' +
                    ", resource_path='" + resource_path + '\'' +
                    ", source_url='" + source_url + '\'' +
                    ", url='" + url + '\'' +
                    ", vid='" + vid + '\'' +
                    '}';
        }
    }
}
