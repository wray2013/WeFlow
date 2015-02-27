package net.zkbc.framework.fep.mdef.repository;

import java.util.List;


import net.zkbc.framework.fep.mdef.entity.MsgMain;

import org.springframework.data.repository.Repository;



public interface MsgMainRepository extends Repository<MsgMain, String> {

	List<MsgMain> findAll();

}
