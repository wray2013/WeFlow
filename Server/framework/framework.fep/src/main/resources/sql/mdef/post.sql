insert into msg_request(
	msg_id,
	id,
	description,
	group_id,
	group_description,
	value_const,
	value_example,
	value_type,
	value_length,
	value_scale,
	va_id1,
	va_params1,
	va_id2,
	va_params2
)
select
	t3.id,
	t1.id,
	t1.description,
	t1.group_id,
	t1.group_description,
	t1.value_const,
	t1.value_example,
	t1.value_type,
	t1.value_length,
	t1.value_scale,
	t1.va_id1,
	t1.va_params1,
	t1.va_id2,
	t1.va_params2
from msg_request t1,msg_main t2,msg_main t3
where t1.msg_id=t2.id and t3.classific=t2.id;

insert into msg_response(
	msg_id,
	id,
	description,
	group_id,
	group_description,
	value_const,
	value_example,
	value_type,
	value_length,
	value_scale
)
select
	t3.id,
	t1.id,
	t1.description,
	t1.group_id,
	t1.group_description,
	t1.value_const,
	t1.value_example,
	t1.value_type,
	t1.value_length,
	t1.value_scale
from msg_response t1,msg_main t2,msg_main t3
where t1.msg_id=t2.id and t3.classific=t2.id;

delete from msg_request where msg_id in(select id from msg_main where classific='T');
delete from msg_response where msg_id in(select id from msg_main where classific='T');
delete from msg_main where classific='T';