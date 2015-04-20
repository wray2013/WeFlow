<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/global.jsp"%>
<c:set target="${self}" property="title" value="${appVars.websiteTitle}-产品维护" />
<c:set target="${self}" property="keywords" value="" />
<c:set target="${self}" property="description" value="" />
<c:set target="${self.css}" property="plugins">
<link rel="stylesheet" type="text/css" href="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/select2/select2.css"/>
<link rel="stylesheet" type="text/css" href="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/datatables/plugins/bootstrap/dataTables.bootstrap.css"/>
</c:set>
<c:set target="${self.css}" property="main">
 <link rel="stylesheet" href="${base}/static/themes/default/css/app.css" />
 <style type="text/css">
	.form-group input{
		border-bottom-left-radius: 0;
    	border-top-left-radius: 0;
	}
 </style>
</c:set>

<c:set target="${self.css}" property="body" value="page-boxed page-header-fixed page-container-bg-solid page-sidebar-closed-hide-logo" />  
<c:set target="${self.content}" property="main">
    <div class="page-content">
				<!-- BEGIN SAMPLE PORTLET CONFIGURATION MODAL FORM-->
				<div class="modal fade" id="portlet-config" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal" aria-hidden="true"></button>
								<h4 class="modal-title">Modal title</h4>
							</div>
							<div class="modal-body">
								 Widget settings form goes here
							</div>
							<div class="modal-footer">
								<button type="button" class="btn blue">Save changes</button>
								<button type="button" class="btn default" data-dismiss="modal">Close</button>
							</div>
						</div>
						<!-- /.modal-content -->
					</div>
					<!-- /.modal-dialog -->
				</div>
				<!-- /.modal -->
				<!-- END SAMPLE PORTLET CONFIGURATION MODAL FORM-->

				<!-- BEGIN PAGE HEADER-->
				<div class="page-bar">
					<ul class="page-breadcrumb">
						<li>
							<i class="fa fa-home"></i>
							<a href="index.html">Home</a>
							<i class="fa fa-angle-right"></i>
						</li>
						<li>
							<a href="#">Data Tables</a>
							<i class="fa fa-angle-right"></i>
						</li>
						<li>
							<a href="#">Managed Datatables</a>
						</li>
					</ul>
					
				</div>
				<!-- END PAGE HEADER-->
				<!-- BEGIN PAGE CONTENT-->
				
				<div class="row">
					<div class="col-md-12 col-sm-12">
						<!-- BEGIN EXAMPLE TABLE PORTLET-->
						<div class="portlet box yellow">
							<div class="portlet-title">
								<div class="caption">
									<i class="fa fa-user"></i>Table
								</div>
								<div class="actions">
									<a href="#" class="btn btn-default btn-sm">
									<i class="fa fa-pencil"></i> Add </a>
									<div class="btn-group">
										<a class="btn btn-default btn-sm" href="#" data-toggle="dropdown">
										<i class="fa fa-cogs"></i> Tools <i class="fa fa-angle-down"></i>
										</a>
										<ul class="dropdown-menu pull-right">
											<li>
												<a href="#">
												<i class="fa fa-pencil"></i> Edit </a>
											</li>
											<li>
												<a href="#">
												<i class="fa fa-trash-o"></i> Delete </a>
											</li>
											<li>
												<a href="#">
												<i class="fa fa-ban"></i> Ban </a>
											</li>
											<li class="divider">
											</li>
											<li>
												<a href="#">
												<i class="i"></i> Make admin </a>
											</li>
										</ul>
									</div>
								</div>
							</div>
							<div class="portlet-body">
								<table class="table table-striped table-bordered table-hover" id="sample_2">
								<thead>
								<tr>
									<th class="table-checkbox">
										<input type="checkbox" class="group-checkable" data-set="#sample_2 .checkboxes"/>
									</th>
									<th>
										 Username
									</th>
									<th>
										 Email
									</th>
									<th>
										 Status
									</th>
								</tr>
								</thead>
								<tbody>
								<tr class="odd gradeX">
									<td>
										<input type="checkbox" class="checkboxes" value="1"/>
									</td>
									<td>
										 shuxer
									</td>
									<td>
										<a href="mailto:shuxer@gmail.com">
										shuxer@gmail.com </a>
									</td>
									<td>
										<span class="label label-sm label-success">
										Approved </span>
									</td>
								</tr>
								<tr class="odd gradeX">
									<td>
										<input type="checkbox" class="checkboxes" value="2"/>
									</td>
									<td>
										 dasr
									</td>
									<td>
										<a href="mailto:shuxer@gmail.com">
										shuxer@gmail.com </a>
									</td>
									<td>
										<span class="label label-sm label-danger">
										adoved </span>
									</td>
								</tr>
								</tbody>
								</table>
							</div>
						</div>
						<!-- END EXAMPLE TABLE PORTLET-->
					</div>
					
				</div>
				<!-- END PAGE CONTENT-->
			</div>

</c:set>

<c:set target="${self.js}" property="plugins">
<script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/uniform/jquery.uniform.min.js" type="text/javascript"></script>
<script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js" type="text/javascript"></script>
<script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/select2/select2.min.js" type="text/javascript"></script>
<script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/datatables/media/js/jquery.dataTables.min.js" type="text/javascript"></script>
<script src="${base}/static/libs/metronic/${version.metronic}/assets/global/plugins/datatables/plugins/bootstrap/dataTables.bootstrap.js" type="text/javascript"></script>
</c:set>

<c:set target="${self.js}" property="main">
<script src="${base}/static/libs/metronic/${version.metronic}/assets/global/scripts/metronic.js" type="text/javascript"></script>
<script src="${base}/static/libs/metronic/${version.metronic}/assets/admin/layout2/scripts/layout.js" type="text/javascript"></script>
<script src="${base}/static/libs/metronic/${version.metronic}/assets/admin/layout2/scripts/demo.js" type="text/javascript"></script>
<script src="${base}/static/libs/metronic/${version.metronic}/assets/admin/pages/scripts/table-managed.js"></script>
<script>
jQuery(document).ready(function() {     
	Metronic.init(); // init metronic core components
	Layout.init(); // init current layout
	Demo.init(); // init demo features
	   TableManaged.init();
});
</script>
</c:set>
<%@ include file="/WEB-INF/views/themes/default/include/main.jsp"%>
