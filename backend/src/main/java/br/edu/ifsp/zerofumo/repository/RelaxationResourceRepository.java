package br.edu.ifsp.zerofumo.repository;

import br.edu.ifsp.zerofumo.entity.RelaxationResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelaxationResourceRepository extends JpaRepository<RelaxationResource, Long> {

    List<RelaxationResource> findByTriggerIgnoreCase(String trigger);
}
