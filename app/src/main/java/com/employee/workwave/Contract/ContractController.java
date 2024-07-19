package com.employee.workwave.Contract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.employee.workwave.exceptions.ServiceValidationException;

import java.util.List;
import java.util.Optional;

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

        @Operation(summary = "Get all contracts", description = "Return a list of all contracts")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Operation successful"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping
        public ResponseEntity<List<Contract>> findAllContracts() {
                List<Contract> allContracts = this.contractService.findAllContracts();
                fullLogsLogger.info("findAllContracts responded with all Contracts.");
                return new ResponseEntity<>(allContracts, HttpStatus.OK);
        }

        @Operation(summary = "Get a contract by ID", description = "Return the details of a contract with a given ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Operation successful"),
                        @ApiResponse(responseCode = "404", description = "contract not found"),
        })
        @GetMapping("/{id}")
        public ResponseEntity<Contract> findContractsById(@PathVariable Long id) {
                Optional<Contract> maybeContract = this.contractService.findById(id);
                Contract foundContract = maybeContract
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Contract not found"));
                fullLogsLogger.info("findContractsById responses with the found Contract:" + foundContract);
                return new ResponseEntity<>(foundContract, HttpStatus.OK);
        }

        @Operation(summary = "Update a Contract by ID", description = "Update a Contract by ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Operation successful"),
                        @ApiResponse(responseCode = "404", description = "Contract not found"),
        })
        @PatchMapping("/{id}")
        public ResponseEntity<Contract> updateContractById(@PathVariable Long id,
                        @Valid @RequestBody UpdateContractDTO data)
                        throws ServiceValidationException {
                Optional<Contract> maybeContract = this.contractService.updateById(id, data);
                Contract updatedContract = maybeContract.orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Contract with id: " + id + " not found"));
                fullLogsLogger.info("updateContractById responses with updated contract:" + updatedContract);
                return new ResponseEntity<>(updatedContract, HttpStatus.OK);
        }

        @Operation(summary = "Delete a contract by ID", description = "Delete a contract by ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Contract deleted"),
                        @ApiResponse(responseCode = "404", description = "Contract not found"),
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteContractById(@PathVariable Long id)
                        throws ServiceValidationException {
                boolean isDeleted = this.contractService.deleteById(id);
                if (!isDeleted) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                        "Contract with id: " + id + " not found");
                }
                fullLogsLogger.info(String.format("Contract with id: %d has been deleted ", id));
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

}
