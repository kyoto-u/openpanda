/**
 * Copyright (c) 2003-2017 The Apereo Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://opensource.org/licenses/ecl2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.gradebookng.tool.panels.importExport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.gradebookng.business.GbCategoryType;
import org.sakaiproject.gradebookng.business.model.GbCourseGrade;
import org.sakaiproject.gradebookng.business.model.GbGradeInfo;
import org.sakaiproject.gradebookng.business.model.GbGroup;
import org.sakaiproject.gradebookng.business.model.GbStudentGradeInfo;
import org.sakaiproject.gradebookng.business.util.EventHelper;
import org.sakaiproject.gradebookng.business.util.FormatHelper;
import org.sakaiproject.gradebookng.tool.model.GradebookUiSettings;
import org.sakaiproject.gradebookng.tool.panels.BasePanel;
import org.sakaiproject.service.gradebook.shared.Assignment;
import org.sakaiproject.service.gradebook.shared.CategoryDefinition;
import org.sakaiproject.service.gradebook.shared.CourseGrade;
import org.sakaiproject.service.gradebook.shared.SortType;
import org.sakaiproject.util.Validator;
import org.sakaiproject.util.api.FormattedText;

import com.opencsv.CSVWriter;

public class ExportPanel extends BasePanel {

	private static final long serialVersionUID = 1L;

	private static final String IGNORE_COLUMN_PREFIX = "#";
	private static final String COMMENTS_COLUMN_PREFIX = "*";
	private static final char CSV_SEMICOLON_SEPARATOR = ';';
	private static final String BOM = "\uFEFF";

	private static final String CSV_EXTENSION = ".csv";
	private static final String EXCEL_EXTENSION_XLS = ".xls";
	private static final String EXCEL_EXTENSION_XLSX = ".xlsx";
	private static final int MAX_COLUMN_SIZE = 255;

	enum ExportFormat {
		CSV, EXCEL
	}

	// default export options
	ExportFormat exportFormat = ExportFormat.CSV;
	boolean includeStudentName = true;
	boolean includeStudentId = true;
	boolean includeStudentNumber = false;
	private boolean includeSectionMembership = false;
	boolean includeStudentDisplayId = false;
	boolean includeGradeItemScores = true;
	boolean includeGradeItemComments = true;
	boolean includeCategoryAverages = false;
	boolean includeCourseGrade = false;
	boolean includePoints = false;
	boolean includeLastLogDate = false;
	boolean includeCalculatedGrade = false;
	boolean includeGradeOverride = false;
	GbGroup group;

	private Component customDownloadLink;
	private Component customDownloadLinkExcel;

	public ExportPanel(final String id) {
		super(id);
	}

	@Override
	public void onInitialize() {
		super.onInitialize();

		add(new AjaxCheckBox("includeStudentId", Model.of(this.includeStudentId)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget ajaxRequestTarget) {
				ExportPanel.this.includeStudentId = !ExportPanel.this.includeStudentId;
				setDefaultModelObject(ExportPanel.this.includeStudentId);
			}
		});
/*
		add(new AjaxCheckBox("includeStudentDisplayId", Model.of(this.includeStudentDisplayId)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget ajaxRequestTarget) {
				ExportPanel.this.includeStudentDisplayId = !ExportPanel.this.includeStudentDisplayId;
				setDefaultModelObject(ExportPanel.this.includeStudentDisplayId);
			}
		});
*/
		add(new AjaxCheckBox("includeStudentName", Model.of(this.includeStudentName)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget ajaxRequestTarget) {
				ExportPanel.this.includeStudentName = !ExportPanel.this.includeStudentName;
				setDefaultModelObject(ExportPanel.this.includeStudentName);
			}
		});
		
		final boolean stuNumVisible = businessService.isStudentNumberVisible();
		add(new AjaxCheckBox("includeStudentNumber", Model.of(this.includeStudentNumber)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget ajaxRequestTarget) {
				ExportPanel.this.includeStudentNumber = !ExportPanel.this.includeStudentNumber;
				setDefaultModelObject(ExportPanel.this.includeStudentNumber);
			}

			@Override
			public boolean isVisible()
			{
				return stuNumVisible;
			}
		});

		add(new AjaxCheckBox("includeSectionMembership", Model.of(this.includeSectionMembership)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget ajaxRequestTarget) {
				ExportPanel.this.includeSectionMembership = !ExportPanel.this.includeSectionMembership;
				setDefaultModelObject(ExportPanel.this.includeSectionMembership);
			}
		});

		add(new AjaxCheckBox("includeGradeItemScores", Model.of(this.includeGradeItemScores)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget ajaxRequestTarget) {
				ExportPanel.this.includeGradeItemScores = !ExportPanel.this.includeGradeItemScores;
				setDefaultModelObject(ExportPanel.this.includeGradeItemScores);
			}
		});
		add(new AjaxCheckBox("includeGradeItemComments", Model.of(this.includeGradeItemComments)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget ajaxRequestTarget) {
				ExportPanel.this.includeGradeItemComments = !ExportPanel.this.includeGradeItemComments;
				setDefaultModelObject(ExportPanel.this.includeGradeItemComments);
			}
		});
		add(new AjaxCheckBox("includePoints", Model.of(this.includePoints)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget ajaxRequestTarget) {
				ExportPanel.this.includePoints = !ExportPanel.this.includePoints;
				setDefaultModelObject(ExportPanel.this.includePoints);
			}

			@Override
			public boolean isVisible() {
				// only allow option if categories are not weighted
				final GbCategoryType categoryType = ExportPanel.this.businessService.getGradebookCategoryType();
				return categoryType != GbCategoryType.WEIGHTED_CATEGORY;
			}
		});
		add(new AjaxCheckBox("includeLastLogDate", Model.of(this.includeLastLogDate)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget ajaxRequestTarget) {
				ExportPanel.this.includeLastLogDate = !ExportPanel.this.includeLastLogDate;
				setDefaultModelObject(ExportPanel.this.includeLastLogDate);
			}
		});
		add(new AjaxCheckBox("includeCategoryAverages", Model.of(this.includeCategoryAverages)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget ajaxRequestTarget) {
				ExportPanel.this.includeCategoryAverages = !ExportPanel.this.includeCategoryAverages;
				setDefaultModelObject(ExportPanel.this.includeCategoryAverages);
			}

			@Override
			public boolean isVisible() {
				return ExportPanel.this.businessService.categoriesAreEnabled();
			}
		});
		add(new AjaxCheckBox("includeCourseGrade", Model.of(this.includeCourseGrade)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget ajaxRequestTarget) {
				ExportPanel.this.includeCourseGrade = !ExportPanel.this.includeCourseGrade;
				setDefaultModelObject(ExportPanel.this.includeCourseGrade);
			}
		});
		add(new AjaxCheckBox("includeCalculatedGrade", Model.of(this.includeCalculatedGrade)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget ajaxRequestTarget) {
				ExportPanel.this.includeCalculatedGrade = !ExportPanel.this.includeCalculatedGrade;
				setDefaultModelObject(ExportPanel.this.includeCalculatedGrade);
			}
		});
		add(new AjaxCheckBox("includeGradeOverride", Model.of(this.includeGradeOverride)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget ajaxRequestTarget) {
				ExportPanel.this.includeGradeOverride = !ExportPanel.this.includeGradeOverride;
				setDefaultModelObject(ExportPanel.this.includeGradeOverride);
			}
		});

		this.group = new GbGroup(null, getString("groups.all"), null, GbGroup.Type.ALL);

		final List<GbGroup> groups = this.businessService.getSiteSectionsAndGroups();
		groups.add(0, this.group);
		add(new DropDownChoice<GbGroup>("groupFilter", Model.of(this.group), groups, new ChoiceRenderer<GbGroup>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object getDisplayValue(final GbGroup g) {
				return g.getTitle();
			}

			@Override
			public String getIdValue(final GbGroup g, final int index) {
				return g.getId();
			}
		}).add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				GbGroup value = (GbGroup) ((DropDownChoice) getComponent()).getDefaultModelObject();
				if (value == null) {
					ExportPanel.this.group = new GbGroup(null, getString("groups.all"), null, GbGroup.Type.ALL);
				} else {
					ExportPanel.this.group = (GbGroup) ((DropDownChoice) getComponent()).getDefaultModelObject();
				}
				// Rebuild the custom download link so it has a filename including the selected group
				Component updatedCustomDownloadLink = buildCustomDownloadLink();
				ExportPanel.this.customDownloadLink.replaceWith(updatedCustomDownloadLink);
				ExportPanel.this.customDownloadLink = updatedCustomDownloadLink;
				target.add(ExportPanel.this.customDownloadLink);
				
				Component updatedCustomDownloadLinkExcel = buildCustomDownloadLinkExcel();
				ExportPanel.this.customDownloadLinkExcel.replaceWith(updatedCustomDownloadLinkExcel);
				ExportPanel.this.customDownloadLinkExcel = updatedCustomDownloadLinkExcel;
				target.add(ExportPanel.this.customDownloadLinkExcel);
			}
		}));

		add(new DownloadLink("downloadFullGradebook", new LoadableDetachableModel<File>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected File load() {
				return buildFile(false);
			}

		}, buildFileName(false, CSV_EXTENSION)).setCacheDuration(Duration.NONE).setDeleteAfterDownload(true));


		this.customDownloadLink = buildCustomDownloadLink();
		add(this.customDownloadLink);


		add(new DownloadLink("downloadFullGradebookExcel", new LoadableDetachableModel<File>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected File load() {
				return buildFileExcel(false);
			}

		}, buildFileName(false, getExtention(false, null))).setCacheDuration(Duration.NONE).setDeleteAfterDownload(true));


		this.customDownloadLinkExcel = buildCustomDownloadLinkExcel();
		add(this.customDownloadLinkExcel);
	}

	private Component buildCustomDownloadLink() {
		return new DownloadLink("downloadCustomGradebook", new LoadableDetachableModel<File>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected File load() {
				return buildFile(true);
			}

		}, buildFileName(true, CSV_EXTENSION)).setCacheDuration(Duration.NONE).setDeleteAfterDownload(true).setOutputMarkupId(true);
	}

	private Component buildCustomDownloadLinkExcel() {
		return new DownloadLink("downloadCustomGradebookExcel", new LoadableDetachableModel<File>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected File load() {
				return buildFileExcel(true);
			}

		}, buildFileName(true, getExtention(true, null))).setCacheDuration(Duration.NONE).setDeleteAfterDownload(true).setOutputMarkupId(true);
	}

	private File buildFile(final boolean isCustomExport) {
		File tempFile;

		try {
			tempFile = File.createTempFile("gradebookTemplate", CSV_EXTENSION);

			//CSV separator is comma unless the comma is the decimal separator, then is ;
			try (OutputStreamWriter fstream = new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8.name())){
				FormattedText formattedText = ComponentManager.get(FormattedText.class);
				CSVWriter csvWriter = new CSVWriter(fstream, ".".equals(formattedText.getDecimalSeparator()) ? CSVWriter.DEFAULT_SEPARATOR : CSV_SEMICOLON_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.RFC4180_LINE_END);

				try {
					fstream.write(BOM);
				} catch(IOException e) {
					// tried
				}

				List<List<Object>> exportData = createExportData(isCustomExport);
				Iterator<List<Object>> dataIter = exportData.iterator();

				while (dataIter.hasNext()) {
					List<Object> rowData = dataIter.next();
					final List<String> row = new ArrayList<>();
					Iterator<Object> colIter = rowData.iterator();
					while (colIter.hasNext()) {
						Object data = colIter.next();
						if (data != null) {
							row.add(data.toString());
						} else {
							row.add("");
						}
					}
					csvWriter.writeNext(row.toArray(new String[] {}));
				}
				csvWriter.close();
			}
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}

		EventHelper.postExportEvent(getGradebook(), isCustomExport);

		return tempFile;
	}


	private String buildFileName(final boolean customDownload, final String extension) {
		final String prefix = getString("importExport.download.filenameprefix");
		final String gradebookName = this.businessService.getGradebook().getName();

		// File name contains the prefix
		final List<String> fileNameComponents = new ArrayList<>();
		fileNameComponents.add(prefix);

		// Add gradebook name/site id to filename
		if (StringUtils.trimToNull(gradebookName) != null) {
			fileNameComponents.add(gradebookName.replaceAll("\\s", "_"));
		}

		// If custom download for all sections, append 'ALL' to filename
		if (customDownload && (this.group == null || this.group.getId() == null)) {
			fileNameComponents.add(getString("importExport.download.filenameallsuffix"));

		// If group/section filter is selected, add group title to filename
		} else if (this.group != null && this.group.getId() != null && StringUtils.isNotBlank(this.group.getTitle())) {
			fileNameComponents.add(this.group.getTitle());
		}

		final String cleanFilename = Validator.cleanFilename(fileNameComponents.stream().collect(Collectors.joining("-")));

		return cleanFilename + extension;
	}



	private String getExtention(final boolean isCustomExport, List<List<Object>> exportData) {
		String extension = null;
		if (CollectionUtils.isEmpty(exportData)) {
			exportData = createExportData(isCustomExport);
		}
		int columns = findColumnSize(exportData);
		if (columns < MAX_COLUMN_SIZE) {
			extension = EXCEL_EXTENSION_XLS;
		} else {
			extension = EXCEL_EXTENSION_XLSX;
		}
		return extension;
	}


	private File buildFileExcel(final boolean isCustomExport) {
		File tempFile;
		
		OutputStream out = null;
		try {
			List<List<Object>> exportData = createExportData(isCustomExport);
			tempFile = File.createTempFile("gradebookTemplate", getExtention(isCustomExport, exportData));
			out = new FileOutputStream(tempFile);
			getAsWorkbook(exportData).write(out);
			out.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (out != null) out.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		EventHelper.postExportEvent(getGradebook(), isCustomExport);

		return tempFile;
	}


	private List<List<Object>> createExportData(final boolean isCustomExport) {
		List<List<Object>> exportData = new ArrayList<List<Object>>();
		FormattedText formattedText = ComponentManager.get(FormattedText.class);
		
		// Create csv header
		final List<Object> header = new ArrayList<>();
		if (!isCustomExport || this.includeStudentId) {
			header.add(getString("importExport.export.csv.headers.studentId"));
		}
		if (!isCustomExport || this.includeStudentName) {
			header.add(getString("importExport.export.csv.headers.studentName"));
		}
		if (isCustomExport && this.includeStudentDisplayId) {
			header.add(String.join(" ", IGNORE_COLUMN_PREFIX, getString("importExport.export.csv.headers.studentDisplayId")));
		}
		if (isCustomExport && this.includeStudentNumber) {
			header.add(String.join(" ", IGNORE_COLUMN_PREFIX, getString("column.header.studentNumber")));
		}
		if (isCustomExport && this.includeSectionMembership) {
			header.add(String.join(" ", IGNORE_COLUMN_PREFIX, getString("column.header.section")));
		}

		// get list of assignments. this allows us to build the columns and then fetch the grades for each student for each assignment from the map
		SortType sortBy = SortType.SORT_BY_SORTING;
		if (this.businessService.categoriesAreEnabled()) {
			sortBy = SortType.SORT_BY_CATEGORY;
		}
		final List<Assignment> assignments = this.businessService.getGradebookAssignments(sortBy);
		final List<CategoryDefinition> categories = this.businessService.getGradebookCategories();

		// no assignments, give a template
		if (assignments.isEmpty()) {
			// with points
			header.add(String.join(" ", getString("importExport.export.csv.headers.example.points"), "[100]"));
			
			// no points
			header.add(getString("importExport.export.csv.headers.example.nopoints"));
			
			// points and comments
			header.add(String.join(" ", COMMENTS_COLUMN_PREFIX, getString("importExport.export.csv.headers.example.pointscomments"), "[50]"));
			
			// ignore
			header.add(String.join(" ", IGNORE_COLUMN_PREFIX, getString("importExport.export.csv.headers.example.ignore")));
		}
		else {
			for (int i = 0; i < assignments.size(); i++) {
				// Pull the next assignment to see if we need to print out a category name 
				final Assignment a1 = assignments.get(i);
				final Assignment a2 = ((i + 1) < assignments.size()) ? assignments.get(i + 1) : null;

				final String assignmentPoints = FormatHelper.formatGradeForDisplay(a1.getPoints().toString());
				if (!isCustomExport || this.includeGradeItemScores) {
					header.add(a1.getName() + " [" + StringUtils.removeEnd(assignmentPoints, formattedText.getDecimalSeparator() + "0") + "]");
				}
				if (!isCustomExport || this.includeGradeItemComments) {
					header.add(String.join(" ", COMMENTS_COLUMN_PREFIX, a1.getName()));
				}
				
				if (isCustomExport && this.includeCategoryAverages
						&& a1.getCategoryId() != null && (a2 == null || !a1.getCategoryId().equals(a2.getCategoryId()))) {
					// Find the correct category in the ArrayList to extract the points
					final CategoryDefinition cd = categories.stream().filter(cat -> a1.getCategoryId().equals(cat.getId())).findAny().orElse(null);
					String catWeightString = "";
					if (cd != null && this.businessService.getGradebookCategoryType() == GbCategoryType.WEIGHTED_CATEGORY) {
						if (cd.getWeight() != null) {
							catWeightString = "(" + FormatHelper.formatDoubleAsPercentage(cd.getWeight() * 100) + ")";
						}
					}

					// Add the category name plus weight if available
					header.add(String.join(" ", IGNORE_COLUMN_PREFIX, getString("label.category"), a1.getCategoryName(), catWeightString));

				}
			}
		}


		if (isCustomExport && this.includePoints) {
			header.add(String.join(" ", IGNORE_COLUMN_PREFIX, getString("importExport.export.csv.headers.points")));
		}
		if (isCustomExport && this.includeCalculatedGrade) {
			header.add(String.join(" ", IGNORE_COLUMN_PREFIX, getString("importExport.export.csv.headers.calculatedGrade")));
		}
		if (isCustomExport && this.includeCourseGrade) {
			header.add(String.join(" ", IGNORE_COLUMN_PREFIX, getString("importExport.export.csv.headers.courseGrade")));
		}
		if (isCustomExport && this.includeGradeOverride) {
			header.add(String.join(" ", IGNORE_COLUMN_PREFIX, getString("importExport.export.csv.headers.gradeOverride")));
		}
		if (isCustomExport && this.includeLastLogDate) {
			header.add(String.join(" ", IGNORE_COLUMN_PREFIX, getString("importExport.export.csv.headers.lastLogDate")));
		}
		
		exportData.add(0, header);

		// apply section/group filter
		final GradebookUiSettings settings = new GradebookUiSettings();
		if (isCustomExport && !GbGroup.Type.ALL.equals(this.group.getType())) {
			settings.setGroupFilter(this.group);
		}

		// get the grade matrix
		final List<GbStudentGradeInfo> grades = this.businessService.buildGradeMatrixForImportExport(assignments, group);

		// add grades
		grades.forEach(studentGradeInfo -> {
			final List<Object> line = new ArrayList<>();
			if (!isCustomExport || this.includeStudentId) {
				line.add(studentGradeInfo.getStudentEid());
			}
			if (!isCustomExport || this.includeStudentName) {
				line.add(FormatHelper.htmlUnescape(studentGradeInfo.getStudentLastName()) + ", " + FormatHelper.htmlUnescape(studentGradeInfo.getStudentFirstName()));
			}
			if (isCustomExport && this.includeStudentDisplayId) {
				line.add(studentGradeInfo.getStudentDisplayId());
			}
			if (isCustomExport && this.includeStudentNumber)
			{
				line.add(studentGradeInfo.getStudentNumber());
			}
			List<String> userSections = studentGradeInfo.getSections();
			if (isCustomExport && this.includeSectionMembership) {
				line.add((userSections.size() > 0) ? userSections.get(0) : getString("sections.label.none"));
			}
			if (!isCustomExport || this.includeGradeItemScores || this.includeGradeItemComments || this.includeCategoryAverages) {
				final Map<Long, Double> categoryAverages = studentGradeInfo.getCategoryAverages();

				for (int i = 0; i < assignments.size(); i++) {
					final Assignment a1 = assignments.get(i);
					final Assignment a2 = ((i + 1) < assignments.size()) ? assignments.get(i + 1) : null;
					final GbGradeInfo gradeInfo = studentGradeInfo.getGrades().get(a1.getId());

					if (gradeInfo != null) {
						if (!isCustomExport || this.includeGradeItemScores) {
							String grade = FormatHelper.formatGradeForDisplay(gradeInfo.getGrade());
							line.add(StringUtils.removeEnd(grade, formattedText.getDecimalSeparator() + "0"));
						}
						if (!isCustomExport || this.includeGradeItemComments) {
							line.add(gradeInfo.getGradeComment());
						}
					} else {
						// Need to account for no grades
						if (!isCustomExport || this.includeGradeItemScores) {
							line.add(null);
						}
						if (!isCustomExport || this.includeGradeItemComments) {
							line.add(null);
						}
					}

					if (isCustomExport && this.includeCategoryAverages
							&& a1.getCategoryId() != null && (a2 == null || !a1.getCategoryId().equals(a2.getCategoryId()))) {
						final Double average = categoryAverages.get(a1.getCategoryId());
						
						final String formattedAverage = FormatHelper.formatGradeForDisplay(average);
						line.add(StringUtils.removeEnd(formattedAverage, formattedText.getDecimalSeparator() + "0"));
					}

				}
			}

			final GbCourseGrade gbCourseGrade = studentGradeInfo.getCourseGrade();
			final CourseGrade courseGrade = gbCourseGrade.getCourseGrade();

			if (isCustomExport && this.includePoints) {
				line.add(FormatHelper.formatGradeForDisplay(FormatHelper.formatDoubleToDecimal(courseGrade.getPointsEarned())));
			}
			if (isCustomExport && this.includeCalculatedGrade) {
				line.add(FormatHelper.formatGradeForDisplay(courseGrade.getCalculatedGrade()));
			}
			if (isCustomExport && this.includeCourseGrade) {
				line.add(courseGrade.getMappedGrade());
			}
			if (isCustomExport && this.includeGradeOverride) {
				line.add(FormatHelper.formatGradeForDisplay(courseGrade.getEnteredGrade()));
			}
			if (isCustomExport && this.includeLastLogDate) {
				if (courseGrade.getDateRecorded() == null) {
					line.add(null);
				} else {
					line.add(this.businessService.formatDateTime(courseGrade.getDateRecorded()));
				}
			}
			
			exportData.add(line);
		});
		
		return exportData;
	}



	public Workbook getAsWorkbook(List<List<Object>> exportData) {
		// outer list is rows, inner list is columns (cells in the row)
		int columns = findColumnSize(exportData);
		Workbook wb;
		if (columns < MAX_COLUMN_SIZE) {
		    wb = new HSSFWorkbook();
		} else {
		    wb = new XSSFWorkbook();
		}

		Sheet sheet = null;

		Iterator<List<Object>> dataIter = exportData.iterator();
		
		short rowPos = 0;
		while (dataIter.hasNext()) {
			List<Object> rowData = dataIter.next();
			if (sheet == null) {
				sheet = wb.createSheet("gradebook"); // avoid NPE
			}
			Row row = sheet.createRow(rowPos++);
			short colPos = 0;
			Iterator<Object> colIter = rowData.iterator();
			while (colIter.hasNext()) {
				Cell cell = null;
				Object data = colIter.next();
				cell = createCell(row, colPos++);
				if (data != null) {
					if (data instanceof Double) {
						cell.setCellValue((Double) data);
					} else if (data instanceof Date) {
						// tell Excel this is a date
						CellStyle style = wb.createCellStyle();
						style.setDataFormat((short) 15);
						cell.setCellStyle(style);
						cell.setCellValue((Date) data);
					} else {
						cell.setCellValue(data.toString());
					}
				}
			}
		}
		
		return wb;
	}


	private int findColumnSize(List<List<Object>> exportData) {
		int columns = 0; // the largest number of columns required for a row
		if (exportData != null && exportData.size() > columns) {
			columns = exportData.size();
		}
		return columns;
	}


	private Cell createCell(Row row, short column) {
		Cell cell = row.createCell(column);
		return cell;
	}
}
