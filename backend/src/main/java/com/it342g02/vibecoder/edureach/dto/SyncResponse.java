package com.it342g02.vibecoder.edureach.dto;

import java.util.List;

public record SyncResponse(int synced, int duplicates, int failed, List<SyncResultDto> results) {
}
