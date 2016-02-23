package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.JsonMessage;
import model.JsonModel;
import model.Task;
import model.TaskComparator;

@RestController
@RequestMapping("/tarefas")
public class TaskController {
	
	static final short BAD_REQUEST = 400;
	static final short OK = 200;

	@RequestMapping(method = RequestMethod.GET)
	public List<Task> getAllTasks() throws IOException {
		List<Task> tasks = new ArrayList<>();

		ObjectMapper mapper = new ObjectMapper();
		Files.walk(Paths.get("tasks/")).forEach(file -> {
			if (file.toString().endsWith(".json")) {
				try {
					String content = new String(Files.readAllBytes(file));
					tasks.add(mapper.readValue(content, Task.class));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return tasks;
	}

	@RequestMapping(method = RequestMethod.POST)
	public JsonModel addTask(@RequestParam(value = "nome") String name,
			@RequestParam(value = "descricao", defaultValue = "") String description) {
		Task task = new Task(name, description);
		String filename = task.getId() + ".json";
		ObjectMapper mapper = new ObjectMapper();
		Task taskFound = (Task) findTask(task.getNome());
		if (task.equals(taskFound))
			return new model.JsonMessage(BAD_REQUEST, "A tarefa já existe"); 
		
		try {
			mapper.writeValue(new File("tasks/" + filename), task);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Não foi possivel serializar o objeto");
		}
		return task;
	}

	@RequestMapping(value = "/{name}", method = RequestMethod.PUT)
	public JsonModel editTask(@PathVariable(value = "name") String name, 
			@RequestParam(value = "descricao") String description) {
		Task task = (Task) findTask(name);
		if (task != null) {
			task.setDescricao(description);
			task.setModificada(new Date());
			writeJson(task);
			return task;
		} else {
			return new model.JsonMessage (BAD_REQUEST, "Tarefa não encontrada");
		}
	}
	
	@RequestMapping(value = "/done/{id}", method = RequestMethod.PUT)
	public JsonModel done(@PathVariable long id) {
		try {
			Task task = getTask(id);
			if (task != null) {
				task.setConcluida();
				task.setModificada(new Date());
				writeJson(task);
				return task;
			} else {
				return new model.JsonMessage(BAD_REQUEST, "Tarefa não encontrada");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new model.JsonMessage(BAD_REQUEST, "Erro ao processar a requisição");
		}
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public JsonModel deleteTask(@PathVariable long id) {
		try {
			Task task = (Task) getTask(id);
			if (task != null) {
				File f = new File("tasks/" + task.getId() + ".json");
				if (f.delete()) {
					return new JsonMessage(OK, "Tarefa apagada");
				}
			} else {
				return new JsonMessage(BAD_REQUEST, "Tarefa não encontrada");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JsonMessage(BAD_REQUEST, "Erro ao processar a requisição");
	}
	
	private boolean writeJson(Task task) {
		if (task == null) {
			throw new NullPointerException("A tarefa é nula");
		}
		String filename = task.getId() + ".json";
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(new File("tasks/" + filename), task);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Não foi possivel serializar o objeto");
		}
		return false;
	}
	
	/**
	 * Find a task by id
	 * @param id Task's id
	 * @return A task or null
	 * @throws IOException
	 */
	private Task getTask(long id) throws IOException {
		File f = new File("tasks/" + id + ".json");
		if (f.exists()) {
			String content = new String(Files.readAllBytes(f.toPath()));
			ObjectMapper mapper = new ObjectMapper();
			Task task = mapper.readValue(content, Task.class);
			return task;
		}
		return null;
	}
	
	/**
	 * Find a task by name
	 * @param name Task's name
	 * @return A task or null
	 */
	private Task findTask(String name) {
		List<Task> tasks = new ArrayList<>();
		int index = -1;
		try {
			tasks = getAllTasks();
			TaskComparator comparator = new TaskComparator();
			Collections.sort(tasks, comparator);
			index = Collections.binarySearch(tasks, new Task(name), comparator);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (index < 0) ? null : tasks.get(index);
	}
}
