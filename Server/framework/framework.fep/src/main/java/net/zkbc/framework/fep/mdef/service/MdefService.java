package net.zkbc.framework.fep.mdef.service;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.zkbc.framework.fep.commons.util.FreemarkerUtils;
import net.zkbc.framework.fep.mdef.entity.MsgBody;
import net.zkbc.framework.fep.mdef.entity.MsgMain;
import net.zkbc.framework.fep.mdef.model.Field;
import net.zkbc.framework.fep.mdef.model.FieldGroup;
import net.zkbc.framework.fep.mdef.model.Message;
import net.zkbc.framework.fep.mdef.model.Root;
import net.zkbc.framework.fep.mdef.repository.MsgMainRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;


import freemarker.template.Configuration;
import freemarker.template.Template;

@Component
@Transactional(readOnly = true)
public class MdefService {

	@Value("${mdef.outputDir}")
	private String outputDir;

	@Value("${mdef.javaPackage}")
	private String javaPackage;

	@Value("${mdef.project}")
	private String project;

	@Value("${mdef.encoding}")
	private String encoding;

	@Value("${mdef.templatesSrc}")
	private String templatesSrc;

	@Value("${mdef.templatesLocation}")
	private String templatesLocation;

	@Autowired
	private MsgMainRepository msgMainRepository;

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public String getJavaPackage() {
		return javaPackage;
	}

	public void setJavaPackage(String javaPackage) {
		this.javaPackage = javaPackage;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void execute() {
		try {
			File targetRoot = new File(outputDir);
			String templatesPattern = templatesLocation + "/" + templatesSrc
					+ "/**/*";
			int pathStart = templatesSrc.length() + 1;

			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			Resource templates = resolver.getResource(templatesLocation);

			Configuration configuration = new Configuration();
			configuration.setDirectoryForTemplateLoading(templates.getFile());

			String templatesURI = templates.getURI().getPath();

			Root root = getRoot();
			String lib = buildInlineLib();

			for (Resource resource : resolver.getResources(templatesPattern)) {
				File srcFile = resource.getFile();

				if (srcFile.isDirectory()) {
					continue;
				}

				String relativeURI = resource.getURI().getPath()
						.substring(templatesURI.length());

				if (isTemplate(relativeURI)) {
					Template template = configuration.getTemplate(relativeURI,
							encoding);
					if (isRootTemplate(relativeURI)) {
						String path = FreemarkerUtils.renderString(relativeURI,
								root);
						File out = new File(targetRoot, path.substring(
								pathStart, path.length() - 9));
						String text = FreemarkerUtils.renderTemplate(template,
								root);

						out.getParentFile().mkdirs();
						FileCopyUtils.copy(text.getBytes(encoding), out);
					} else {
						for (Message message : root.getMessages()) {
							String path = FreemarkerUtils.renderString(lib
									+ relativeURI, message);
							File out = new File(targetRoot, path.substring(
									pathStart, path.length() - 4));
							String text = FreemarkerUtils.renderTemplate(
									template, message);

							out.getParentFile().mkdirs();
							FileCopyUtils.copy(text.getBytes(encoding), out);
						}
					}
				} else {
					File out = new File(targetRoot,
							FreemarkerUtils.renderString(
									relativeURI.substring(pathStart), root));
					out.getParentFile().mkdirs();

					if (out.isHidden()) {
						out.createNewFile();
					}

					FileCopyUtils.copy(srcFile, out);
				}
			}
		} catch (Throwable t) {
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	private String buildInlineLib() {
		StringBuffer buf = new StringBuffer();
		buf.append("<#function upper_case str><#return str?upper_case></#function>");
		buf.append("<#function lower_case str><#return str?lower_case></#function>");
		buf.append("<#function cap_first str><#return str?cap_first></#function>");
		return buf.toString();
	}

	private boolean isTemplate(String relativeURI) {
		return relativeURI.endsWith(".ftl");
	}

	private boolean isRootTemplate(String relativeURI) {
		return relativeURI.endsWith(".root.ftl");
	}

	private Root getRoot() {
		Root root = new Root();
		root.setJavaPackage(javaPackage);
		root.setProject(project);

		List<Message> messages = new ArrayList<Message>();
		root.setMessages(messages);

		for (MsgMain msg : msgMainRepository.findAll()) {
			Message message = new Message();
			messages.add(message);

			message.setRoot(root);
			message.setId(msg.getId());
			message.setDescription(msg.getDescription());
			message.setRequestFields(new ArrayList<Field>());
			message.setRequestGroups(new ArrayList<FieldGroup>());
			message.setResponseFields(new ArrayList<Field>());
			message.setResponseGroups(new ArrayList<FieldGroup>());

			buildFieldsAndGroups(
					new ArrayList<MsgBody>(msg.getRequestElements()),
					message.getRequestFields(), message.getRequestGroups());
			buildFieldsAndGroups(
					new ArrayList<MsgBody>(msg.getResponseElements()),
					message.getResponseFields(), message.getResponseGroups());
		}

		return root;
	}

	private void buildFieldsAndGroups(List<MsgBody> elems, List<Field> fields,
			List<FieldGroup> groups) {
		Map<String, FieldGroup> groupMap = new LinkedHashMap<String, FieldGroup>();
		for (MsgBody elem : elems) {
			String groupId = elem.getGroupId();
			if (groupId == null || "".equals(groupId)) {
				appendToFields(elem, fields);
			} else {
				FieldGroup group = groupMap.get(groupId);
				if (group == null) {
					group = new FieldGroup();
					groupMap.put(groupId, group);

					group.setId(groupId);
					group.setDescription(elem.getGroupDescription());
					group.setFields(new ArrayList<Field>());
				}
				appendToFields(elem, group.getFields());
			}
		}
		groups.addAll(groupMap.values());
	}

	private void appendToFields(MsgBody elem, List<Field> fields) {
		Field field = new Field();
		fields.add(field);

		field.setId(elem.getId());
		field.setDescription(elem.getDescription());
		field.setValueConst(elem.getValueConst());
		field.setValueExample(elem.getValueExample());
		field.setDbType(elem.getValueType().getDbType());
		field.setJavaType(elem.getValueType().getJavaType());
		field.setObjcType(elem.getValueType().getObjcType());
		field.setValueLength(elem.getValueLength());
		field.setValueScale(elem.getValueScale());
		if (isVa(elem.getVaId1())) {
			field.setVaId1(elem.getVaId1());
			field.setVaParams1(elem.getVaParams1());
		}
		if (isVa(elem.getVaId2())) {
			field.setVaId2(elem.getVaId2());
			field.setVaParams2(elem.getVaParams2());
		}
	}

	private boolean isVa(String vaId) {
		return vaId != null && vaId.startsWith("@");
	}

}
