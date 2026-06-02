package com.taxidispatcher.services.dispatcher.domain.dispatch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DispatchRepository {
    Dispatch save(Dispatch dispatch);

    Optional<Dispatch> findById(DispatchId dispatchId);

    Page<Dispatch> findByCustomerId(CustomerId customerId, Pageable pageable);

    Page<Dispatch> findByStatus(DispatchStatus status, Pageable pageable);

    List<Dispatch> findAllByStatus(DispatchStatus status);
}
