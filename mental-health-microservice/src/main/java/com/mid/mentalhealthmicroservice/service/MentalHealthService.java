package com.mid.mentalhealthmicroservice.service;

import com.mid.mentalhealthmicroservice.dto.CategoryBasedExerciseDTO;
import com.mid.mentalhealthmicroservice.dto.MentalExerciseDTO;
import com.mid.mentalhealthmicroservice.dto.MentalHealthRecommendationDTO;
import com.mid.mentalhealthmicroservice.dto.QuestionsDTO;
import com.mid.mentalhealthmicroservice.entity.CategoryBasedExerciseEntity;
import com.mid.mentalhealthmicroservice.entity.MentalExerciseEntity;
import com.mid.mentalhealthmicroservice.entity.UserInformationEntity;
import com.mid.mentalhealthmicroservice.exception.ExerciseNotFound;
import com.mid.mentalhealthmicroservice.exception.UserNotFound;
import com.mid.mentalhealthmicroservice.repository.CategoryBasedExerciseRepository;
import com.mid.mentalhealthmicroservice.repository.MentalExerciseRepository;
import com.mid.mentalhealthmicroservice.repository.UserInformationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MentalHealthService {
    @Autowired
    MentalExerciseRepository mentalExerciseRepository;
    @Autowired
    CategoryBasedExerciseRepository categoryBasedExerciseRepository;
    @Autowired
    UserInformationRepository userInformationRepository;
    public MentalExerciseDTO createMentalExercise(MentalExerciseDTO mentalExerciseDTO){
        MentalExerciseEntity mentalExercise=new MentalExerciseEntity();
        mentalExercise.setExercise(mentalExerciseDTO.getExercise());
        mentalExercise.setDescription(mentalExerciseDTO.getDescription());
        return new ModelMapper().map(mentalExerciseRepository.save(mentalExercise),MentalExerciseDTO.class);
    }
    public List<MentalExerciseDTO> getAllMentalExercise(){
        List<MentalExerciseDTO> mentalExerciseDTOList=new ArrayList<>();
        for(MentalExerciseEntity mentalExerciseEntity:mentalExerciseRepository.findAll()){
            mentalExerciseDTOList.add(new ModelMapper().map(mentalExerciseEntity,MentalExerciseDTO.class));
        }
        return mentalExerciseDTOList.stream()
                .sorted(Comparator.comparing(MentalExerciseDTO::getExercise))
                .collect(Collectors.toList());
    }
    public MentalExerciseDTO getMentalExercise (String exercise) throws ExerciseNotFound {
        if(mentalExerciseRepository.existsByExercise(exercise)){
            return new ModelMapper().map(mentalExerciseRepository.findByExercise(exercise).orElseThrow(() -> new NullPointerException("No exercise")),MentalExerciseDTO.class);
        }
        throw new ExerciseNotFound();
    }
    public CategoryBasedExerciseDTO getUserBasedMentalExercise(Integer userId) throws UserNotFound{
        if(userInformationRepository.existsByUserId(userId)) {
            CategoryBasedExerciseEntity categoryBasedExerciseEntity=categoryBasedExerciseRepository.findByCategory(userInformationRepository.findByUserId(userId).orElseThrow(() -> new NullPointerException()).getMentalHealthCategory()).orElseThrow(() -> new NullPointerException());
            CategoryBasedExerciseDTO categoryBasedExerciseDTO=new ModelMapper().map(categoryBasedExerciseEntity,CategoryBasedExerciseDTO.class);

            List<MentalExerciseDTO> mentalExerciseDTOList=new ArrayList<>();
            for(MentalExerciseEntity mentalExerciseEntity:categoryBasedExerciseEntity.getMentalExerciseEntities()){
                mentalExerciseDTOList.add(new ModelMapper().map(mentalExerciseEntity,MentalExerciseDTO.class));
            }
            categoryBasedExerciseDTO.setExercises(mentalExerciseDTOList);
            return categoryBasedExerciseDTO;
        }
        throw new UserNotFound();
    }
    public MentalHealthRecommendationDTO getUserBasedRecommendation(Integer userId) throws UserNotFound {
        if(userInformationRepository.existsByUserId(userId)) {
            CategoryBasedExerciseEntity categoryBasedExerciseEntity=categoryBasedExerciseRepository.findByCategory(userInformationRepository.findByUserId(userId).orElseThrow(() -> new NullPointerException()).getMentalHealthCategory()).orElseThrow(() -> new NullPointerException());
            MentalHealthRecommendationDTO mentalHealthRecommendationDTO=new ModelMapper().map(categoryBasedExerciseEntity,MentalHealthRecommendationDTO.class);
            return mentalHealthRecommendationDTO;
        }
        throw new UserNotFound();
    }

    public Boolean findMentalHealth(QuestionsDTO questionsDTO,Integer userId){
        if(userInformationRepository.existsByUserId(userId)){
            return false;
        }
        else{
            //*******Check if exists in user-profile-service
            //*******If exists then proceed with next parts otherwise print, the user doesn't exist
            UserInformationEntity userInformationEntity=new UserInformationEntity();
            userInformationEntity.setUserId(userId);

            if(questionsDTO.getSleep()==0 && questionsDTO.getStress()==0 && questionsDTO.getAnxiety()==0){
                userInformationEntity.setMentalHealthCategory("Normal");
                userInformationRepository.save(userInformationEntity);
            }
            else if (questionsDTO.getSleep()==0 && questionsDTO.getStress()==0 && questionsDTO.getAnxiety()==1) {
                userInformationEntity.setMentalHealthCategory("Anxious");
                userInformationRepository.save(userInformationEntity);
            }
            else if (questionsDTO.getSleep()==0 && questionsDTO.getStress()==1 && questionsDTO.getAnxiety()==0) {
                userInformationEntity.setMentalHealthCategory("Stressed");
                userInformationRepository.save(userInformationEntity);
            }
            else if (questionsDTO.getSleep()==1 && questionsDTO.getStress()==0 && questionsDTO.getAnxiety()==0) {
                userInformationEntity.setMentalHealthCategory("Insomniac");
                userInformationRepository.save(userInformationEntity);
            }
            else if (questionsDTO.getSleep()==0 && questionsDTO.getStress()==1 && questionsDTO.getAnxiety()==1) {
                userInformationEntity.setMentalHealthCategory("Turbulent");
                userInformationRepository.save(userInformationEntity);
            }
            else if (questionsDTO.getSleep()==1 && questionsDTO.getStress()==1 && questionsDTO.getAnxiety()==0) {
                userInformationEntity.setMentalHealthCategory("Stressinsomnia");
                userInformationRepository.save(userInformationEntity);
            }

            else if (questionsDTO.getSleep()==1 && questionsDTO.getStress()==0 && questionsDTO.getAnxiety()==1) {
                userInformationEntity.setMentalHealthCategory("Anxiosomnia");
                userInformationRepository.save(userInformationEntity);
            }

            else if (questionsDTO.getSleep()==1 && questionsDTO.getStress()==1 && questionsDTO.getAnxiety()==1) {
                userInformationEntity.setMentalHealthCategory("Severe");
                userInformationRepository.save(userInformationEntity);
            }
            return true;
        }

    }

    public void populateCategory(){
        List<MentalExerciseEntity> mentalExerciseEntitiesList=new ArrayList<>();
        CategoryBasedExerciseEntity categoryBasedExerciseEntity1=new CategoryBasedExerciseEntity();
        categoryBasedExerciseEntity1.setCategory("Normal");
        categoryBasedExerciseEntity1.setDuration("5-10 minutes");
        categoryBasedExerciseEntity1.setRecommendation("1.Maintain regular sleep routines \n 2.Follow suggested Mental health exercises");
        mentalExerciseEntitiesList.add(mentalExerciseRepository.findById(1).get());
        categoryBasedExerciseEntity1.setMentalExerciseEntities(mentalExerciseEntitiesList);
        categoryBasedExerciseRepository.save(categoryBasedExerciseEntity1);

        CategoryBasedExerciseEntity categoryBasedExerciseEntity2=new CategoryBasedExerciseEntity();
        categoryBasedExerciseEntity2.setCategory("Anxious");
        categoryBasedExerciseEntity2.setDuration("10-15 minutes");
        categoryBasedExerciseEntity2.setRecommendation("1.Make sure to get 6-8 hours sleep \n 2.Limit caffeine and alcohol\n 3.Follow suggested Mental health exercises");
        mentalExerciseEntitiesList=new ArrayList<>();
        mentalExerciseEntitiesList.add(mentalExerciseRepository.findById(2).get());
        mentalExerciseEntitiesList.add(mentalExerciseRepository.findById(4).get());
        categoryBasedExerciseEntity2.setMentalExerciseEntities(mentalExerciseEntitiesList);
        categoryBasedExerciseRepository.save(categoryBasedExerciseEntity2);

        CategoryBasedExerciseEntity categoryBasedExerciseEntity3=new CategoryBasedExerciseEntity();
        categoryBasedExerciseEntity3.setCategory("Stressed");
        categoryBasedExerciseEntity3.setDuration("10-15 minutes");
        categoryBasedExerciseEntity3.setRecommendation("1.Make sure to get 6-8 hours sleep \n 2.Maintain a stress journal\n 3.Follow suggested Mental health exercises");
        mentalExerciseEntitiesList=new ArrayList<>();
        mentalExerciseEntitiesList.add(mentalExerciseRepository.findById(2).get());
        mentalExerciseEntitiesList.add(mentalExerciseRepository.findById(5).get());
        categoryBasedExerciseEntity3.setMentalExerciseEntities(mentalExerciseEntitiesList);
        categoryBasedExerciseRepository.save(categoryBasedExerciseEntity3);

        CategoryBasedExerciseEntity categoryBasedExerciseEntity4=new CategoryBasedExerciseEntity();
        categoryBasedExerciseEntity4.setCategory("Insomniac");
        categoryBasedExerciseEntity4.setDuration("10-15 minutes");
        categoryBasedExerciseEntity4.setRecommendation("1.Go to bed and and wake up at the same time everyday \n 2.Exercise regularly during the day\n 3.Follow suggested Mental health exercises");
        mentalExerciseEntitiesList=new ArrayList<>();
        mentalExerciseEntitiesList.add(mentalExerciseRepository.findById(1).get());
        mentalExerciseEntitiesList.add(mentalExerciseRepository.findById(3).get());
        categoryBasedExerciseEntity4.setMentalExerciseEntities(mentalExerciseEntitiesList);
        categoryBasedExerciseRepository.save(categoryBasedExerciseEntity4);

        CategoryBasedExerciseEntity categoryBasedExerciseEntity5=new CategoryBasedExerciseEntity();
        categoryBasedExerciseEntity5.setCategory("Turbulent");
        categoryBasedExerciseEntity5.setDuration("15-20 minutes");
        categoryBasedExerciseEntity5.setRecommendation("1.Make sure to get 6-8 hours sleep \n 2.Give self affirmation everyday\n 3.Follow suggested Mental health exercises");
        mentalExerciseEntitiesList=new ArrayList<>();
        mentalExerciseEntitiesList.add(mentalExerciseRepository.findById(1).get());
        mentalExerciseEntitiesList.add(mentalExerciseRepository.findById(2).get());
        mentalExerciseEntitiesList.add(mentalExerciseRepository.findById(4).get());
        categoryBasedExerciseEntity5.setMentalExerciseEntities(mentalExerciseEntitiesList);
        categoryBasedExerciseRepository.save(categoryBasedExerciseEntity5);

        CategoryBasedExerciseEntity categoryBasedExerciseEntity6=new CategoryBasedExerciseEntity();
        categoryBasedExerciseEntity6.setCategory("Stressinsomnia");
        categoryBasedExerciseEntity6.setDuration("15-20 minutes");
        categoryBasedExerciseEntity6.setRecommendation("1.Go to bed and and wake up at the same time everyday \n 2.Maintain a journal \n 3.Follow suggested Mental health exercises");
        mentalExerciseEntitiesList=new ArrayList<>();
        mentalExerciseEntitiesList.add(mentalExerciseRepository.findById(1).get());
        mentalExerciseEntitiesList.add(mentalExerciseRepository.findById(3).get());
        categoryBasedExerciseEntity6.setMentalExerciseEntities(mentalExerciseEntitiesList);
        categoryBasedExerciseRepository.save(categoryBasedExerciseEntity6);

        CategoryBasedExerciseEntity categoryBasedExerciseEntity7=new CategoryBasedExerciseEntity();
        categoryBasedExerciseEntity7.setCategory("Anxiosomnia");
        categoryBasedExerciseEntity7.setDuration("15-20 minutes");
        categoryBasedExerciseEntity7.setRecommendation("1.Go to bed and and wake up at the same time everyday \n 2.Limit caffeine and alcohol\n 3.Follow suggested Mental health exercises");
        mentalExerciseEntitiesList=new ArrayList<>();
        mentalExerciseEntitiesList.add(mentalExerciseRepository.findById(1).get());
        mentalExerciseEntitiesList.add(mentalExerciseRepository.findById(6).get());
        categoryBasedExerciseEntity7.setMentalExerciseEntities(mentalExerciseEntitiesList);
        categoryBasedExerciseRepository.save(categoryBasedExerciseEntity7);

        CategoryBasedExerciseEntity categoryBasedExerciseEntity8=new CategoryBasedExerciseEntity();
        categoryBasedExerciseEntity8.setCategory("Severe");
        categoryBasedExerciseEntity8.setDuration("20-25 minutes");
        categoryBasedExerciseEntity8.setRecommendation("1.Go to bed and and wake up at the same time everyday \n 2.Limit caffeine and alcohol\n 3.Give self affirmation everyday\n 4.Follow suggested Mental health exercises");
        mentalExerciseEntitiesList=new ArrayList<>();
        mentalExerciseEntitiesList.add(mentalExerciseRepository.findById(1).get());
        mentalExerciseEntitiesList.add(mentalExerciseRepository.findById(2).get());
        mentalExerciseEntitiesList.add(mentalExerciseRepository.findById(7).get());
        categoryBasedExerciseEntity8.setMentalExerciseEntities(mentalExerciseEntitiesList);
        categoryBasedExerciseRepository.save(categoryBasedExerciseEntity8);
    }
    public void populateExercise(){

        MentalExerciseEntity mentalExercise1=new MentalExerciseEntity();
        mentalExercise1.setExercise("Mindfulness Meditation");
        mentalExercise1.setDescription("Find a quiet and comfortable place to sit or lie down.Close your eyes and focus your attention on your breath. Breathe naturally and pay close attention to the sensation of each breath.");
        mentalExerciseRepository.save(mentalExercise1);

        MentalExerciseEntity mentalExercise2=new MentalExerciseEntity();
        mentalExercise2.setExercise("Deep Breathing Exercises");
        mentalExercise2.setDescription("Practice deep breathing to activate the body's relaxation response. One technique is the 4-7-8 breath, where you inhale for a count of 4, hold for 7, and exhale for 8.");
        mentalExerciseRepository.save(mentalExercise2);

        MentalExerciseEntity mentalExercise3=new MentalExerciseEntity();
        mentalExercise3.setExercise("Progressive Muscle Relaxation");
        mentalExercise3.setDescription("To practice PMR, lie down in a comfortable position and start at your toes, progressively working your way up to your head. Tense each muscle group for a few seconds, then release and relax.");
        mentalExerciseRepository.save(mentalExercise3);

        MentalExerciseEntity mentalExercise4=new MentalExerciseEntity();
        mentalExercise4.setExercise("Yoga");
        mentalExercise4.setDescription("Yoga is a practice that combines physical postures, breath control, meditation, and ethical principles to promote physical, mental, and spiritual well-being.");
        mentalExerciseRepository.save(mentalExercise4);

        MentalExerciseEntity mentalExercise5=new MentalExerciseEntity();
        mentalExercise5.setExercise("Meditation");
        mentalExercise5.setDescription("Meditation is a practice that involves focusing your mind on a particular object, thought, or activity to achieve a state of mental clarity, relaxation, and heightened awareness. ");
        mentalExerciseRepository.save(mentalExercise5);

        MentalExerciseEntity mentalExercise6=new MentalExerciseEntity();
        mentalExercise6.setExercise("Cognitive-Behavioral Therapy");
        mentalExercise6.setDescription("It includes various techniques and exercises, such as cognitive restructuring and stimulus control");
        mentalExerciseRepository.save(mentalExercise6);

        MentalExerciseEntity mentalExercise7=new MentalExerciseEntity();
        mentalExercise7.setExercise("Visualization");
        mentalExercise7.setDescription("Close your eyes and visualize a calming and safe place. Imagine yourself in this place, engaging all your senses to make it as vivid as possible. Spend a few minutes here.");
        mentalExerciseRepository.save(mentalExercise7);

    }
}
