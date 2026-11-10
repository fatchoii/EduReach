package com.it342g02.vibecoder.edureach.dto;

import java.util.List;

public record StudentMasteryDto(Long studentId, String studentName, double overall, List<SkillMasteryDto> skills) {
}
