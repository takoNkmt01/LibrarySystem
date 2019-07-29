package com.example.demo.login.domain.repository.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.login.domain.model.Book;
import com.example.demo.login.domain.model.BookRegistForm;

@Mapper
public interface BookMapper {
	public int searchPublisher(String publisherName);//出版社が存在するか確かめて
	public void insertPublisher(String publisherName);//なければ出版社を登録し
	public Integer getPublisherId(String publisherName);
	public boolean insertBook(BookRegistForm form, int publisherId, String today);
	public Book selectOneBook(String isbn);
	public int count();
	public List<Book> selectAllBook(int divNum);
	public boolean updateOne(Book book, int publisher_id, String today);
	public boolean deleteOne(Book book, String today);
	public boolean changeLB(Book book, String today);
}
