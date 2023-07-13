package com.bestcodingstager.slang.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="�˻� ����� �����ϴ�.")
public class SearchException extends RuntimeException{

}
