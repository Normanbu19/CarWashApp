package com.example.carwashapp;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class VolleyMultipartRequest extends Request<String> {

    private final Response.Listener<String> mListener;
    private final Response.ErrorListener mErrorListener;
    private final String boundary = "----CarWashBoundary" + System.currentTimeMillis();
    private final String lineEnd = "\r\n";
    private final String twoHyphens = "--";

    public VolleyMultipartRequest(int method, String url,
                                  Response.Listener<String> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            // 🔹 Parámetros normales
            Map<String, String> params = getParams();
            if (params != null) {
                for (String key : params.keySet()) {
                    bos.write((twoHyphens + boundary + lineEnd).getBytes());
                    bos.write(("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd).getBytes());
                    bos.write(lineEnd.getBytes());
                    bos.write(params.get(key).getBytes());
                    bos.write(lineEnd.getBytes());
                }
            }

            // 🔹 Archivos (imágenes)
            Map<String, DataPart> data = getByteData();
            if (data != null) {
                for (String key : data.keySet()) {
                    DataPart part = data.get(key);

                    bos.write((twoHyphens + boundary + lineEnd).getBytes());
                    bos.write(("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + part.getFileName() + "\"" + lineEnd).getBytes());
                    bos.write(("Content-Type: " + part.getType() + lineEnd).getBytes());
                    bos.write(lineEnd.getBytes());

                    bos.write(part.getData());

                    bos.write(lineEnd.getBytes());
                }
            }

            bos.write((twoHyphens + boundary + twoHyphens + lineEnd).getBytes());

            return bos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error al crear multipart: " + e.getMessage());
        }
    }

    @Override
    protected Response<String> parseNetworkResponse(@NonNull NetworkResponse response) {
        String result;
        try {
            result = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (Exception e) {
            result = new String(response.data);
        }
        return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(@NonNull VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    protected Map<String, DataPart> getByteData() {
        return null;
    }

    public static class DataPart {
        private final String fileName;
        private final byte[] data;
        private final String type;

        public DataPart(String fileName, byte[] data, String type) {
            this.fileName = fileName;
            this.data = data;
            this.type = type;
        }

        public String getFileName() { return fileName; }
        public byte[] getData() { return data; }
        public String getType() { return type; }
    }
}
