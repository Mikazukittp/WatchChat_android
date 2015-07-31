package app.android.ttp.mikazuki.watchchat.data.api.model;

import app.android.ttp.mikazuki.watchchat.data.api.model.BaseAPIResponse;

/**
 * Created by haijimakazuki on 15/07/13.
 */
public class SamplePushResponse extends BaseAPIResponse {
    private String result;

    public SamplePushResponse() {
    }

    public SamplePushResponse(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
