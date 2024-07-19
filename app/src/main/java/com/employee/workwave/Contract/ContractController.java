package com.employee.workwave.Contract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee.workwave.exceptions.ServiceValidationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Contract", description = "Endpoints for managing contracts.")
@RestController
@RequestMapping("/api/v1/contracts")
public class ContractController {
     @Autowired
        private ContractService contractService;

        private static final Logger fullLogsLogger = LogManager.getLogger("fullLogs");

        
        @Operation(summary = "Create a new contract", description = "Create a new contract and return contract details")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Contract created"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        @PostMapping
        public ResponseEntity<Contract> createContract(@Valid @RequestBody CreateContractDTO data)
                        throws ServiceValidationException {
                Contract createdContract = this.contractService.createContract(data);
                fullLogsLogger.info("createContract responded with new Contract: " + createdContract);
                return new ResponseEntity<>(createdContract, HttpStatus.CREATED);
        }


}
