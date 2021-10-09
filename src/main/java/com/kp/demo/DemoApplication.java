package com.kp.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(StudentRepository repository, MongoTemplate mongoTemplate) {
		return args -> {
			Address address = new Address(
					"England",
					"London",
					"NE9"
			);
			Student student=new Student(
					"Anna",
					"Reeds",
					"areeds@reeds.com",
					Gender.FEMALE,
					address,
					List.of("Computer science", "Maths"),
					BigDecimal.TEN,
					LocalDate.now()
			);
			//usingMongoTemplateAndQuery(repository, mongoTemplate, student);
			repository.findStudentByEmail(student.getEmail())
					.ifPresentOrElse(s -> {
						System.out.println(student +" already exists");
					}, () ->{
						System.out.println("Inserting student "+ student);
						repository.insert(student);
					});
		};
	}

	private void usingMongoTemplateAndQuery(StudentRepository repository, MongoTemplate mongoTemplate, Student student) {
		Query query = new Query();
		query.addCriteria(Criteria.where("email").is(student.getEmail()));

		List<Student> students = mongoTemplate.find(query, Student.class);

		if (students.size()>1){
			throw new IllegalStateException("Found many students with email "+ student.getEmail());
		}
		if (students.isEmpty()) {
			System.out.println("Inserting student "+ student);
			repository.insert(student);
		} else {
			System.out.println(student +" already exists");
		}
	}

}
