package com.ft.mapp.utils;

import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class SimpleDownloadListener implements DownloadListener {
    @Override
    public void taskStart(@NonNull DownloadTask task) {

    }

    @Override
    public void connectTrialStart(@NonNull DownloadTask task, @NonNull
            Map<String, List<String>> requestHeaderFields) {

    }

    @Override
    public void connectTrialEnd(@NonNull DownloadTask task, int responseCode, @NonNull
            Map<String, List<String>> responseHeaderFields) {

    }

    @Override
    public void downloadFromBeginning(@NonNull DownloadTask task, @NonNull BreakpointInfo info, @NonNull ResumeFailedCause cause) {

    }

    @Override
    public void downloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info) {

    }

    @Override
    public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull
            Map<String, List<String>> requestHeaderFields) {

    }

    @Override
    public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull
            Map<String, List<String>> responseHeaderFields) {

    }

    @Override
    public void fetchStart(@NonNull DownloadTask task, int blockIndex, long contentLength) {

    }

    @Override
    public void fetchProgress(@NonNull DownloadTask task, int blockIndex, long increaseBytes) {

    }

    @Override
    public void fetchEnd(@NonNull DownloadTask task, int blockIndex, long contentLength) {

    }

    @Override
    public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable
            Exception realCause) {

    }
}
