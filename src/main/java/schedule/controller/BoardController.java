package schedule.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import schedule.model.Board;
import schedule.service.BoardService;

import java.util.List;

@Controller
@RequestMapping("/boards")

public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // 게시글 목록
    @GetMapping
    public String list(Model model) {
        model.addAttribute("boards", boardService.findAll());
        return "boards/list";
    }

    // 게시글 상세 보기
    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("board", boardService.findById(id));
        return "boards/view";
    }

    // 새 글 작성 폼
    @GetMapping("/new")
    public String newBoardForm() {
        return "boards/new";
    }

    // 새 글 작성
    @PostMapping
    public String create(@RequestBody Board board) {
        boardService.save(board);
        return "redirect:/boards";
    }

    // 게시글 수정 폼
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("board", boardService.findById(id));
        return "boards/edit";
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Board board) {
        // 기존 데이터 가져오기
        Board existingBoard = boardService.findById(id);
        existingBoard.setTitle(board.getTitle());
        existingBoard.setContent(board.getContent());
        existingBoard.setWriter(board.getWriter());

        // 데이터 저장
        boardService.save(existingBoard);
        return "redirect:/boards";
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        boardService.deleteById(id);
        return "redirect:/boards";
    }
}
