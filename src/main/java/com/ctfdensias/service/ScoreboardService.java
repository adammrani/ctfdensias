package com.ctfdensias.service;

import com.ctfdensias.dto.response.ScoreboardEntry;
import java.util.List;

public interface ScoreboardService {
    List<ScoreboardEntry> getScoreboard();
    List<ScoreboardEntry> getScoreboardAdmin();
}
