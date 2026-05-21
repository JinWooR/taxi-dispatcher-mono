package com.taxidispatcher.services.dispatcher.domain.dispatch;

import java.util.Optional;

public interface DispatchRepository {
    void save(Dispatch dispatch);
    Optional<Dispatch> findById(DispatchId dispatchId);
    void update(Dispatch dispatch);
}
