package com.almerys.columbia.api.services;

import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaDefinition;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class ExcelService {

  private final ContextService contextService;

  private final DefinitionService definitionService;

  public ExcelService(ContextService contextService, DefinitionService definitionService) {
    this.contextService = contextService;
    this.definitionService = definitionService;
  }

  public XSSFWorkbook createExcel(HttpServletResponse httpServletResponse, Long contextId) {
    XSSFWorkbook workbook = null;

    //Récupération du contexte
    ColumbiaContext context = contextService.getById(contextId);
    if (context == null) {
      throw new IllegalArgumentException("Context does not exists");
    }

    httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + "context.xlsx");

    //Récupération des définitions
    Iterable<ColumbiaDefinition> definitions = definitionService.getByContextId(contextId);

    try {
      workbook = new XSSFWorkbook(XSSFWorkbookType.XLSX);

      workbook.createSheet(context.getName());
      XSSFSheet rankerSheet1 = workbook.getSheetAt(0);
      writeExcelOutputData(rankerSheet1, definitions);
      workbook.write(httpServletResponse.getOutputStream());
    } catch (Exception e) {
      //Ign
    }

    return workbook;
  }


  private void writeExcelOutputData(XSSFSheet rankerSheet, Iterable<ColumbiaDefinition> definitions) {
    XSSFRow row = rankerSheet.createRow(0);
    row.createCell(0).setCellValue("Terme");
    row.createCell(1).setCellValue("Définition");
    row.createCell(2).setCellValue("Abréviations");
    row.createCell(3).setCellValue("Synonymes");
    row.createCell(4).setCellValue("Antonymes");
    row.createCell(5).setCellValue("Termes liés");
    row.createCell(6).setCellValue("Sources");
    row.createCell(7).setCellValue("Bibliographie");
    row.createCell(8).setCellValue("RGPD");

    int count = 1;
    for (ColumbiaDefinition def : definitions) {
      row = rankerSheet.createRow(count);
      count += 1;
      row.createCell(0).setCellValue(def.getTerm().getName());
      row.createCell(1).setCellValue(def.getDefinition());

      StringBuilder abb = new StringBuilder();
      def.getTerm().getAbbreviations().forEach(a -> abb.append(a).append(","));
      String abbrs = (abb.toString().length() >= 1) ? abb.toString().substring(0, abb.toString().length() - 1) : "";
      row.createCell(2).setCellValue(abbrs);

      StringBuilder syn = new StringBuilder();
      def.getSynonymsTermList().forEach(a -> syn.append(a.getName()).append(","));
      String syns = (syn.toString().length() >= 1) ? syn.toString().substring(0, syn.toString().length() - 1) : "";
      row.createCell(3).setCellValue(syns);

      StringBuilder ant = new StringBuilder();
      def.getAntonymsTermList().forEach(a -> ant.append(a.getName()).append(","));
      String ants = (ant.toString().length() >= 1) ? ant.toString().substring(0, ant.toString().length() - 1) : "";
      row.createCell(4).setCellValue(ants);

      StringBuilder rel = new StringBuilder();
      def.getRelatedTermList().forEach(a -> rel.append(a.getName()).append(","));
      String rels = (rel.toString().length() >= 1) ? rel.toString().substring(0, rel.toString().length() - 1) : "";
      row.createCell(5).setCellValue(rels);

      StringBuilder sou = new StringBuilder();
      def.getSources().forEach(a -> sou.append(a).append(","));
      String sous = (sou.toString().length() >= 1) ? sou.toString().substring(0, sou.toString().length() - 1) : "";
      row.createCell(6).setCellValue(sous);

      StringBuilder bib = new StringBuilder();
      def.getBibliography().forEach(a -> bib.append(a).append(","));
      String bibs = (bib.toString().length() >= 1) ? bib.toString().substring(0, bib.toString().length() - 1) : "";
      row.createCell(7).setCellValue(bibs);

      row.createCell(8).setCellValue(def.getGdpr() ? "Oui" : "Non");
    }

  }

}
