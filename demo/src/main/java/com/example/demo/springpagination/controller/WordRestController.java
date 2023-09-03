package com.example.demo.springpagination.controller;

import com.example.demo.springpagination.dto.WordDto;
import com.example.demo.springpagination.model.Word;
import com.example.demo.springpagination.service.IWordService;
import com.example.demo.springpagination.service.IUserService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/words")
public class WordRestController {
	
	@Autowired
    private IWordService wordService;
 
    @Autowired
    private IUserService userService;
 
    @Autowired
    private ModelMapper modelMapper;
 
    // 단어 홈 (모든 단어)
    @GetMapping
    @ResponseBody
    public List<WordDto> getWords(
            @PathVariable("page") int page,
            @PathVariable("size") int size, 
            @PathVariable("sortDir") String sortDir, 
            @PathVariable("sort") String sort) {
        
        List<Word> words = wordService.getWordsList(page, size, sortDir, sort);
        return words.stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    }
    
    // 인기 단어
    @GetMapping
    @ResponseBody
    public List<WordDto> getPopularWords() {
        
        List<Word> words = wordService.getPopularWordsList();
        return words.stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    }
    
    // 최신 단어
    @GetMapping
    @ResponseBody
    public List<WordDto> getNewestWords() {
        
        List<Word> words = wordService.getNewestWordsList();
        return words.stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    }
    
    /*
    @GetMapping("/writer_id={writer_id}")
    @ResponseBody
    public List<WordDto> getUserWords(
            @PathVariable("page") int page,
            @PathVariable("size") int size, 
            @PathVariable("sortDir") String sortDir, 
            @PathVariable("sort") String sort) {
        
        List<Word> words = wordService.getUserWordsList(page, size, sortDir, sort);
        return words.stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    }
    */
 
    // 단어 추가
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public WordDto createword(@RequestBody WordDto wordDto) throws ParseException {
    	Word word = convertToEntity(wordDto);
    	Word wordCreated = wordService.createWord(word);
        return convertToDto(wordCreated);
        // 여기다 단어 db에 업데이트하는 함수 추가
    }
 
    // 단어 상세 정보
    @GetMapping(value = "/word_id={word_id}")
    @ResponseBody
    public WordDto getWord(@PathVariable("word_id") Long word_id) {
        return convertToDto(wordService.getWordById(word_id));
    }
 
    // 단어 수정 (권한 있는 경우)
    @PatchMapping(value = "/word_id={word_id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateword(@PathVariable("word_id") Long word_id, @RequestBody WordDto wordDto) throws ParseException {
        if(!Objects.equals(word_id, wordDto.getWord_id())){
            throw new IllegalArgumentException("IDs don't match");
        }

        Word word = convertToEntity(wordDto);
        wordService.updateWord(word);
    }
	
	@DeleteMapping(path="/word_id={word_id}")
	@ResponseStatus(HttpStatus.OK)
	public void getDeleteWordForm(@PathVariable("word_id") Long word_id) {
		wordService.deleteWord(word_id);
	}
	
	@GetMapping(path="/name={name}")
	public List<Word> getWordsByName(@PathVariable String name) {
		return wordService.getWordsByName(name);
	}

    // DTO 변환
    private WordDto convertToDto(Word word) {
    	WordDto wordDto = modelMapper.map(word, WordDto.class);
        wordDto.setWrittenDate(word.getWrittenDate(), 
            userService.getCurrentUser().getPreference().getTimezone());
        return wordDto;
    }
    
    // Entity 변환
    private Word convertToEntity(WordDto wordDto) throws ParseException {
    	Word word = modelMapper.map(wordDto, Word.class);
        word.setWrittenDate(wordDto.getWrittenDateConverted(
          userService.getCurrentUser().getPreference().getTimezone()));
      
        if (wordDto.getWord_id() != null) {
        	Word oldWord = wordService.getWordById(wordDto.getWord_id());
            word.setWriter_id(oldWord.getWriter_id());
            word.setSent(oldWord.isSent());
        }
        return word;
    }

}