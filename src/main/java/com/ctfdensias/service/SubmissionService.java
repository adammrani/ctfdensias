package com.ctfdensias.service;

import com.ctfdensias.dto.request.SubmitFlagRequest;
import com.ctfdensias.dto.response.SubmissionResponse;
import com.ctfdensias.model.User;

public interface SubmissionService {
    SubmissionResponse submitFlag(SubmitFlagRequest request, User currentUser);
}
