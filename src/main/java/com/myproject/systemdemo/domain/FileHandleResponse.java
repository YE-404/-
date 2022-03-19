package com.myproject.systemdemo.domain;

public class FileHandleResponse {
    /** 上传状态，0：失败，1：上传成功 */
    private int success;

    /** 图片上传提示信息,包括上传成功或上传失败及错误信息等 */
    private String message;

    /** 图片上传成功后返回的地址 */
    private String url;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "FileHandleResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
