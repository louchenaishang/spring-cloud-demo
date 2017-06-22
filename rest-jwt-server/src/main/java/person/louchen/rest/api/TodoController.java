package person.louchen.rest.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by louchen on 2017/6/22.
 */
@RestController
@RequestMapping("/todos")
@PreAuthorize("hasRole('USER')")
public class TodoController {

    @GetMapping("")
    public String toDoList(){
        return "toDoList";
    }
}
