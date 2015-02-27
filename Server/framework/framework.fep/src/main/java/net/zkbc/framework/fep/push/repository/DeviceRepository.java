package net.zkbc.framework.fep.push.repository;


import net.zkbc.framework.fep.push.entity.MobileDevice;

import org.springframework.data.repository.CrudRepository;


public interface DeviceRepository extends CrudRepository<MobileDevice, Long> {
}
