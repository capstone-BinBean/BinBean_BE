# ☕ 빈빈 (BinBean)
> **AI 기반 실시간 좌석 현황 서비스**

<img width="400" height="380" alt="Frame 104" src="https://github.com/user-attachments/assets/b9576b9f-a5a5-4d04-bb17-4972e9c24c33" />


## 📱 서비스 개요
> **AI 기반 실시간 좌석 현황 서비스**를 통해 카페 이용 편의성을 혁신적으로 개선하는 모바일 애플리케이션

- CCTV로 캡처된 이미지를 **AWS Rekognition**의 객체 인식 기술로 분석하고, 이를 카페 관리자가 사전에 설정한 디지털 도면과 연동하여 좌석 점유 현황을 실시간으로 시각화합니다.  
- **Gemini Flash 2.0** 모델의 멀티모달 처리 능력을 활용하여 CCTV 이미지와 좌석 도면을 매핑, 사용자는 앱을 통해 카페 혼잡도를 직관적으로 확인할 수 있습니다.  
- 이를 통해 카페 방문 전 빈자리 확인이 가능하며, 운영자는 좌석 점유 데이터를 기반으로 운영 전략을 개선할 수 있습니다.
---

## 🛠️ 기술 스택
- **프론트엔드**: Android (Kotlin)
- **백엔드**:
  - AWS Rekognition (객체 탐지)
  - Gemini Flash 2.0 (멀티모달 매핑)
- **데이터 처리**:
  - CCTV 이미지 캡처 및 base64 인코딩
  - 좌석 좌표 JSON 기반 시각화
- **기타**:
  - OpenCV (향후 전처리 및 보정용)

---

## 🚀 주요 기능 & 스크린샷
- **좌석 점유 분석**
  - CCTV 이미지 → AWS Rekognition으로 사람 위치 검출
  - Gemini Flash 2.0으로 좌석 도면과 매핑
  - 결과를 JSON으로 구조화하여 클라이언트에 제공

- **앱 사용자 경험 흐름**
  1. 사용자 위치 기반 카페 지도 표시
  2. 카페 선택 → 도면 기반 좌석 현황 조회
  3. 실시간 업데이트된 빈자리/혼잡도 정보 확인

| **위치 기반 <br/> 카페 지도 표시** | **카페 상세정보 페이지** | **카페 좌석현황 페이지** | **카페 리뷰페이지** |
|:---:|:---:|:---:|:---:|
| <img width="400" height="800" alt="메인화면_바텀시트" src="https://github.com/user-attachments/assets/f096ad83-c1a1-4483-bc44-7e6edf7c6a5b" /> | <img width="400" height="800" alt="메인화면_바텀시트_상세" src="https://github.com/user-attachments/assets/5180680b-d8d7-46be-bd62-678b5692a84f" /> | <img width="400" height="800" alt="메인화면_좌석확인" src="https://github.com/user-attachments/assets/689c5b13-6393-4a71-b4ca-9429035e960c" /> | <img width="400" height="800" alt="메인화면_바텀시트_리뷰" src="https://github.com/user-attachments/assets/a787ebc7-c56e-4422-bdee-ce4d5adf1d6a" /> |
| 사용자의 현재 위치를 중심으로 주변 카페를 지도에 표시하고, 원하는 카페 선택가능 | 카페 이름, 주소, 운영시간 등 기본 정보 확인 & 상세페이지 이동 | 카페 내부 도면과 함께 좌석별 이용 가능 여부를 실시간으로 시각화 | 이용자들이 남긴 후기와 평점을 확인하고 직접 리뷰를 작성가능 |

---

## 👥 빈빈팀 소개

<table align="center">
 <tr align="center">
     <td><B>김병수<B></td>
     <td><B>민송경<B></td>
     <td><B>윤채원<B></td>
     <td><B>이아림<B></td>
     <td><B>최혜선<B></td>
 </tr>
 <tr align="center">
     <td>
         <a href="https://github.com/GamJaDo">
            <img src="https://github.com/GamJaDo.png" style="max-width: 100px">
         </a>
         <br>
         <a href="https://github.com/GamJaDo"><B>Backend</B></a>
     </td>
     <td>
         <a href="https://github.com/miiiiiin">
         <img src="https://github.com/miiiiiin.png" style="max-width: 100px">
         </a>
         <br>
         <a href="https://github.com/miiiiiin"><B>Backend</B></a>
     </td>
     <td>
         <a href="https://github.com/settle54">
         <img src="https://github.com/settle54.png" style="max-width: 100px">
         </a>
         <br>
         <a href="https://github.com/settle54"><B>Android</B></a>
     </td>
     <td>
         <a href="https://github.com/arieum">
         <img src="https://github.com/arieum.png" style="max-width: 100px">
         </a>
         <br>
         <a href="https://github.com/arieum"><B>Android</B></a>
     </td>
     <td>
         <a href="https://github.com/hyeseon-cpu">
         <img src="https://github.com/hyeseon-cpu.png" style="max-width: 100px">
         </a>
         <br>
         <a href="https://github.com/hyeseon-cpu"><B>Design</B></a>
     </td>
 </tr>
</table>
