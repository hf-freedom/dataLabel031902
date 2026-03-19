package com.todo;

import com.todo.cache.TodoCache;
import com.todo.cache.UserCache;
import com.todo.common.Result;
import com.todo.dto.TodoCreateDTO;
import com.todo.dto.TodoUpdateDTO;
import com.todo.entity.Priority;
import com.todo.entity.Todo;
import com.todo.entity.TodoStatus;
import com.todo.entity.User;
import com.todo.service.TodoService;
import com.todo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TodoApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserCache userCache;

    @Autowired
    private TodoCache todoCache;

    @Autowired
    private UserService userService;

    @Autowired
    private TodoService todoService;

    @BeforeEach
    void setUp() {
        todoCache.findAll().forEach(t -> todoCache.deleteById(t.getId()));
    }

    @Test
    void testUserInitialization() {
        List<User> users = userService.findAll();
        assertNotNull(users);
        assertTrue(users.size() >= 4, "应该至少有4个用户(1管理员+3普通用户)");
    }

    @Test
    void testAdminUserExists() {
        User admin = userService.findById(1L);
        assertNotNull(admin);
        assertTrue(admin.getIsAdmin());
        assertEquals("admin", admin.getUsername());
    }

    @Test
    void testCreateTodo() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("userId", "2");

        TodoCreateDTO dto = new TodoCreateDTO();
        dto.setTitle("测试待办");
        dto.setContent("这是一个测试待办");
        dto.setExecutorId(2L);
        dto.setPriorityCode(2);
        dto.setDueTime(System.currentTimeMillis() + 3600000);

        HttpEntity<TodoCreateDTO> entity = new HttpEntity<>(dto, headers);
        ResponseEntity<Result> response = restTemplate.postForEntity("/api/todo/create", entity, Result.class);

        assertEquals(200, response.getBody().getCode());
    }

    @Test
    void testGetMyTodos() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("userId", "2");

        TodoCreateDTO dto = new TodoCreateDTO();
        dto.setTitle("我的待办");
        dto.setContent("测试获取我的待办");
        dto.setExecutorId(2L);
        dto.setPriorityCode(2);

        HttpEntity<TodoCreateDTO> createEntity = new HttpEntity<>(dto, headers);
        restTemplate.postForEntity("/api/todo/create", createEntity, Result.class);

        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ResponseEntity<Result> response = restTemplate.exchange("/api/todo/my", HttpMethod.GET, getEntity, Result.class);

        assertEquals(200, response.getBody().getCode());
    }

    @Test
    void testUpdateTodo() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("userId", "2");

        TodoCreateDTO createDto = new TodoCreateDTO();
        createDto.setTitle("原始标题");
        createDto.setContent("原始内容");
        createDto.setExecutorId(2L);
        createDto.setPriorityCode(2);

        HttpEntity<TodoCreateDTO> createEntity = new HttpEntity<>(createDto, headers);
        ResponseEntity<Result> createResponse = restTemplate.postForEntity("/api/todo/create", createEntity, Result.class);
        
        Todo created = todoService.findByExecutorId(2L).get(0);
        assertNotNull(created);

        TodoUpdateDTO updateDto = new TodoUpdateDTO();
        updateDto.setId(created.getId());
        updateDto.setTitle("更新后的标题");
        updateDto.setPriorityCode(3);

        HttpEntity<TodoUpdateDTO> updateEntity = new HttpEntity<>(updateDto, headers);
        ResponseEntity<Result> updateResponse = restTemplate.exchange("/api/todo/update", HttpMethod.PUT, updateEntity, Result.class);

        assertEquals(200, updateResponse.getBody().getCode());
    }

    @Test
    void testDeleteTodo() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("userId", "2");

        TodoCreateDTO createDto = new TodoCreateDTO();
        createDto.setTitle("待删除的待办");
        createDto.setContent("测试删除");
        createDto.setExecutorId(2L);

        HttpEntity<TodoCreateDTO> createEntity = new HttpEntity<>(createDto, headers);
        restTemplate.postForEntity("/api/todo/create", createEntity, Result.class);

        Todo created = todoService.findByExecutorId(2L).get(0);

        HttpEntity<Void> deleteEntity = new HttpEntity<>(headers);
        ResponseEntity<Result> deleteResponse = restTemplate.exchange("/api/todo/delete/" + created.getId(), HttpMethod.DELETE, deleteEntity, Result.class);

        assertEquals(200, deleteResponse.getBody().getCode());
        assertNull(todoService.findById(created.getId()));
    }

    @Test
    void testUpdatePriority() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("userId", "2");

        TodoCreateDTO createDto = new TodoCreateDTO();
        createDto.setTitle("测试优先级");
        createDto.setExecutorId(2L);

        HttpEntity<TodoCreateDTO> createEntity = new HttpEntity<>(createDto, headers);
        restTemplate.postForEntity("/api/todo/create", createEntity, Result.class);

        Todo created = todoService.findByExecutorId(2L).get(0);

        HttpEntity<Void> updateEntity = new HttpEntity<>(headers);
        ResponseEntity<Result> response = restTemplate.exchange(
                "/api/todo/priority/" + created.getId() + "?priorityCode=3",
                HttpMethod.PUT, updateEntity, Result.class);

        assertEquals(200, response.getBody().getCode());
    }

    @Test
    void testAdminPushTodo() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("userId", "1");

        TodoCreateDTO dto = new TodoCreateDTO();
        dto.setTitle("管理员推送的待办");
        dto.setContent("测试管理员推送");
        dto.setExecutorId(2L);
        dto.setPriorityCode(3);

        HttpEntity<TodoCreateDTO> entity = new HttpEntity<>(dto, headers);
        ResponseEntity<Result> response = restTemplate.postForEntity("/api/admin/push-todo", entity, Result.class);

        assertEquals(200, response.getBody().getCode());
    }

    @Test
    void testAdminGetAllUsers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("userId", "1");

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Result> response = restTemplate.exchange("/api/admin/users", HttpMethod.GET, entity, Result.class);

        assertEquals(200, response.getBody().getCode());
    }

    @Test
    void testNonAdminCannotAccessAdminApi() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("userId", "2");

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Result> response = restTemplate.exchange("/api/admin/users", HttpMethod.GET, entity, Result.class);

        assertEquals(403, response.getBody().getCode());
    }

    @Test
    void testTodoServiceDirectly() {
        Todo todo = new Todo();
        todo.setTitle("直接测试待办");
        todo.setContent("直接调用Service测试");
        todo.setCreatorId(1L);
        todo.setCreatorName("admin");
        todo.setExecutorId(2L);
        todo.setExecutorName("zhangsan");
        todo.setPriority(Priority.HIGH);
        todo.setDueTime(new Date(System.currentTimeMillis() + 3600000));

        Todo created = todoService.create(todo);
        assertNotNull(created.getId());

        Todo found = todoService.findById(created.getId());
        assertNotNull(found);
        assertEquals("直接测试待办", found.getTitle());

        List<Todo> userTodos = todoService.findByExecutorId(2L);
        assertFalse(userTodos.isEmpty());

        todoService.delete(created.getId());
        assertNull(todoService.findById(created.getId()));
    }
}
