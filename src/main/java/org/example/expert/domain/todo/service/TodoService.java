package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.command.GetTodosCommand;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

	private final TodoRepository todoRepository;
	private final WeatherClient weatherClient;

	@Transactional
	public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
		User user = User.fromAuthUser(authUser);

		String weather = weatherClient.getTodayWeather();

		Todo newTodo = new Todo(
				todoSaveRequest.getTitle(),
				todoSaveRequest.getContents(),
				weather,
				user);
		Todo savedTodo = todoRepository.save(newTodo);

		return new TodoSaveResponse(
				savedTodo.getId(),
				savedTodo.getTitle(),
				savedTodo.getContents(),
				weather,
				new UserResponse(user.getId(), user.getEmail()));
	}

	public Page<TodoResponse> getTodos(GetTodosCommand getTodosCommand) {
		Pageable pageable = PageRequest.of(getTodosCommand.page() - 1, getTodosCommand.size());

		LocalDateTime startDate;
		LocalDateTime endDate;

		if (getTodosCommand.endDate() == null) {
			endDate = LocalDateTime.now().plusDays(1);
		} else {
			endDate = getTodosCommand.endDate().plusDays(1).atStartOfDay();
		}

		if(getTodosCommand.startDate() == null) {
			// 가능한 제일 오래된 년도
			startDate = LocalDateTime.of(1970, 1, 1, 0, 0);
		} else {
			startDate = getTodosCommand.startDate().atStartOfDay();
		}

		Page<Todo> todos;

		if(getTodosCommand.weather() == null) {
			todos = todoRepository.searchBetweenDates(startDate, endDate, pageable);
		} else {
			String weatherKeyword = "%" + getTodosCommand.weather() + "%";
			todos = todoRepository.searchWithWeatherBetweenDates(weatherKeyword, startDate, endDate, pageable);
		}

		return todos.map(todo -> new TodoResponse(
				todo.getId(),
				todo.getTitle(),
				todo.getContents(),
				todo.getWeather(),
				new UserResponse(todo.getUser().getId(), todo.getUser().getEmail()),
				todo.getCreatedAt(),
				todo.getModifiedAt()));
	}

	public TodoResponse getTodo(long todoId) {
		Todo todo = todoRepository.findByIdWithUser(todoId)
				.orElseThrow(() -> new InvalidRequestException("Todo not found"));

		User user = todo.getUser();

		return new TodoResponse(
				todo.getId(),
				todo.getTitle(),
				todo.getContents(),
				todo.getWeather(),
				new UserResponse(user.getId(), user.getEmail()),
				todo.getCreatedAt(),
				todo.getModifiedAt());
	}
}
