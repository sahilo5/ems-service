//package com.SadhyaSiddhi.ems_service.services;
//
//import com.SadhyaSiddhi.ems_service.dto.studentDto;
//import com.SadhyaSiddhi.ems_service.models.student;
//import com.SadhyaSiddhi.ems_service.repository.StudentRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class StudentServiceImpl implements StudentService {
//
//    private StudentRepository studentRepository;
//
//    @Autowired
//    public StudentServiceImpl(StudentRepository studentRepository) {
//        this.studentRepository = studentRepository;
//    }
//
//    @Override
//    public boolean createStudent(studentDto studentDto) {
//        student stud = new student();
//        stud.setName(studentDto.getName());
//        stud.setEmail(studentDto.getEmail());
//
//        studentRepository.save(stud);
//        System.out.println("Student saved successfully");
//        return true;
//    }
//
//    @Override
//    public List<student> getAllStudents() {
//        return studentRepository.findAll();
//    }
//
//}
