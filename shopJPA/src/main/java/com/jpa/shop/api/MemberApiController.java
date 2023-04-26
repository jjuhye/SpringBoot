package com.jpa.shop.api;

import com.jpa.shop.domain.Member;
import com.jpa.shop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberApiController {
    private final MemberService memberService;

    @GetMapping("/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();   /// 객체 리스트 반환 -> json 메시지컴버터 작동 -> json 리스트로 넘겨준다
    }
    @GetMapping("/v2/members")
    public Result membersV2(){
        List<Member> members = memberService.findMembers(); // 순수 member 클래스의 리스트 member -> memberDTO
        List<MemberDto> list = members.stream().map(m-> new MemberDto(m.getName())).collect(Collectors.toList());
        return new Result(list.size(),list);
    }
    @Data
    @AllArgsConstructor
    static class Result<T>{
        private int count;
        private T data;
    }
    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }

    @PostMapping("/v1/members")
    // requset -> json -> 객체  return 객체 -> json
    public CreateMemberResponse saveMemberV1(@RequestBody Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @Data
    @AllArgsConstructor
    static class CreateMemberResponse{
        private Long id;
    }
    @PostMapping("/v2/members")
    // requset -> json -> 객체  return 객체 -> json
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }
    @Data
    static class CreateMemberRequest{
        @NotBlank(message = "이름입력 필수")
        private String name;
        @ConstructorProperties({"name"})
        public CreateMemberRequest(String name){
            this.name = name;
        }
    }


    @Data
    static class UpdateMemberRequest{
        @NotBlank(message = "이름입력 필수")
        private String name;
        @ConstructorProperties({"name"})
        public UpdateMemberRequest(String name){
            this.name = name;
        }
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }


    // 부분 수정
    @PatchMapping("/v2/members/{id}")
    public UpdateMemberResponse updateMember(
            @PathVariable Long id,
            @RequestBody UpdateMemberRequest request ){

        // 수정할때는 변경감지사용하자
        memberService.update( id, request.getName());  // 커멘드 -> 수정
        // 쿼리문 따로 부르기
        Member member = memberService.findOne(id);
        return new UpdateMemberResponse(member.getId(), member.getName());
    }
}
