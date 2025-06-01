package server.poptato.user.domain.repository;

import server.poptato.user.domain.entity.DeleteReason;

public interface DeleteReasonRepository {

    DeleteReason save(DeleteReason deleteReason);
}
