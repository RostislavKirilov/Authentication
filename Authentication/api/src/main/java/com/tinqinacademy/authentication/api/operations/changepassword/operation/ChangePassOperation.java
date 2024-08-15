package com.tinqinacademy.authentication.api.operations.changepassword.operation;

import com.tinqinacademy.authentication.api.base.OperationProcessor;
import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.operations.changepassword.input.ChangePassInput;
import com.tinqinacademy.authentication.api.operations.changepassword.output.ChangePassOutput;
import io.vavr.control.Either;

import org.springframework.stereotype.Service;

@Service
public interface ChangePassOperation extends OperationProcessor<ChangePassInput, ChangePassOutput> {
}