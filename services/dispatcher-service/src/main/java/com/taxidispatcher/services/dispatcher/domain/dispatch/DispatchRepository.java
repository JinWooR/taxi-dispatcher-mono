package com.taxidispatcher.services.dispatcher.domain.dispatch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DispatchRepository {
    Dispatch save(Dispatch dispatch);

    Optional<Dispatch> findById(DispatchId dispatchId);

    Page<Dispatch> findByUserId(UserId userId, Pageable pageable);

    Page<Dispatch> findByStatus(DispatchStatus status, Pageable pageable);
}
