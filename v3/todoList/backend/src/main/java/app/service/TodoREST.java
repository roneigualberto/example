package app.service;

import app.entity.Todo;
import app.message.TodoMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.demoiselle.jee.core.api.crud.Result;
import org.demoiselle.jee.core.api.security.DemoiselleUser;
import org.demoiselle.jee.crud.AbstractREST;
import org.demoiselle.jee.crud.Search;
import org.demoiselle.jee.crud.pagination.ResultSet;
import org.demoiselle.jee.rest.exception.DemoiselleRestException;
import org.demoiselle.jee.security.annotation.Authenticated;
import org.demoiselle.jee.security.annotation.RequiredRole;

/**
 *
 * @author gladson
 */
@Api("v1/Todo")
@Path("v1/todo")
@Authenticated
public class TodoREST extends AbstractREST<Todo, String> {

    @Inject
    private DemoiselleUser dml;

    @Inject
    private TodoMessage message;

    @POST
    @Override
    @Transactional
    @ApiOperation(value = "persist entity")
    public Todo persist(@Valid Todo entity) {
        if (entity.getUser().getId().equalsIgnoreCase(dml.getIdentity())) {
            return bc.persist(entity);
        } else {
            throw new DemoiselleRestException(message.onlyOwner(), 403);
        }
    }

    @PUT
    @Override
    @Transactional
    @ApiOperation(value = "full update entity")
    public Todo mergeFull(@Valid Todo entity) {
        if (entity.getUser().getId().equalsIgnoreCase(dml.getIdentity())) {
            return bc.mergeFull(entity);
        } else {
            throw new DemoiselleRestException(message.onlyOwner(), 403);
        }
    }

    @DELETE
    @Override
    @Path("{id}")
    @Transactional
    @ApiOperation(value = "remove entity")
    public void remove(@PathParam("id") final String id) {
        Todo todo = bc.find(id);
        if (todo.getUser().getId().equalsIgnoreCase(dml.getIdentity())) {
            bc.remove(id);
        } else {
            throw new DemoiselleRestException(message.onlyOwner(), 403);
        }
    }

    @GET
    @Override
    @Transactional
    @Search(fields = {"*"})
    public Result find() {
        Result res = new ResultSet();
        res.setContent((List<?>) bc.find(dml.getIdentity()).getUser().getTodos());
        return res;
    }

    @GET
    @Override
    @Path("{id}")
    @Transactional
    @ApiOperation(value = "find by ID")
    public Todo find(@PathParam("id") final String id) {
        Todo todo = bc.find(id);
        if (todo.getUser().getId().equalsIgnoreCase(dml.getIdentity())) {
            return bc.find(id);
        } else {
            throw new DemoiselleRestException(message.onlyOwner(), 403);
        }
    }

}
