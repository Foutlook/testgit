drop table data_colleaction;

create table data_collection(
	id number constraint date_colleaction_id_pk primary key,
	aaa_login_name varchar2(20) constraint date_collection_login_name_nn not null,
	login_ip varchar2(32) constraint date_collection_ip_nn not null,
	login_date date,
	logout_date date,
	nas_ip varchar2(32),
	time_duration number(10)
);