package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.services.ExcelService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;


@Controller
@Api(tags = "Export Controller", description = "Controller to export context to a XLSX format")
@RequestMapping(value = "/contexts")
public class ExporterController extends AbstractRestController {

  private final ExcelService excelOutputService;
  private final ColumbiaConfiguration columbiaConfiguration;

  public ExporterController(ExcelService excelOutputService, ColumbiaConfiguration columbiaConfiguration) {
    this.excelOutputService = excelOutputService;
    this.columbiaConfiguration = columbiaConfiguration;
  }

  @JsonIgnore
  @ApiOperation(value = "Export a defined context", authorizations = @Authorization(value="Authentication", scopes = {}))
  @GetMapping(value = "/{contextId}/export")
  public ResponseEntity downloadExcelOutputExl(Authentication authentication, HttpServletResponse response, @ApiParam(value="Context identifier", required = true)  @PathVariable(value = "contextId") Long contextId) {

    if (!hasRole(authentication, columbiaConfiguration.getAdminRoleName(), "CONTEXT_" + contextId)) {
      return forbidden();
    }

    return ResponseEntity.ok(excelOutputService.createExcel(response, contextId));
  }
}
