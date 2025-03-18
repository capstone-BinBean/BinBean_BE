name: "request_issues_template"
description: "요청 사항을 작성하는 템플릿입니다."
title: "request:"
labels: []
assignees: []

body:
  - type: input
    id: related_issue
    attributes:
      label: "연관 이슈 번호"
      description: "관련된 이슈 번호를 입력해주세요."
      placeholder: "#123"

  - type: textarea
    id: request_content
    attributes:
      label: "요청 내용"
      description: "논의해야 할 부분 또는 수정 요청 사항 등을 적어주세요."
      placeholder: |
        예: 
        - 특정 기능의 UI 수정 요청
        - API 응답 데이터 구조 변경 필요
    validations:
      required: true
