package com.hansung.android.calendarproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MonthViewActivity extends AppCompatActivity {

    ArrayList<String> dayList; // 달력 정보를 담고 있는 리스트
    int firstDay; // 첫 날의 요일
    int totDays; //이 달의 마지막 날짜
    int iYear; //현재 년도
    int iMonth; //현재 월


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //이전버튼을 누르나 다음버튼을 누르나 새로운 액티비티가 뜨는데 하여튼 이전 액티비티로부터 받은 연도와 월 정보
        Intent intent = getIntent();
        iYear = intent.getIntExtra("year", -1);
        iMonth = intent.getIntExtra("month", -1);

        // iYear나 iMonth의 default값이 -1이면 (즉, 이 전 액티비티로부터 받은 값이 없을 경우)맨 처음 화면이므로 오늘의 년, 월을 받아온다.
        Calendar calendar = Calendar.getInstance();
        if ( iYear == -1 || iMonth == -1 ) {
            iYear = calendar.get(Calendar.YEAR);
            iMonth = calendar.get(Calendar.MONTH);
        }

        //todayView는 매 달마다의 년, 월 정보를 표시한다.
        TextView todayView = (TextView) findViewById(R.id.today);
        todayView.setText( String.format("%d년 %d월", iYear, iMonth + 1) );

        //dayList 초기화
        dayList = new ArrayList<String>();
        setCalendar(iYear, iMonth); //iYear와 iMonth를 바탕으로 dayList에 데이터를 채움.

        //그리드 어댑터에 dayList를 넣어줌.
        GridAdapter gridAdapter = new GridAdapter(getApplicationContext(), dayList);
        GridView gridView = findViewById(R.id.gridview);
        gridView.setAdapter(gridAdapter); //그리드뷰에 어댑터설정.

        //이전 버튼
        Button btn = (Button)findViewById(R.id.pre);
        //이전 버튼 눌렀을 시 이벤트 발생.
        btn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (iMonth==0) { //1월달에서 이전 버튼을 눌렀을 경우 12월달로 이전
                    iMonth = 11;
                    iYear--;
                }
                else //1월이 아닌 달은 1씩 줄이기.
                    iMonth--;

                //새로운 인텐트를 띄울 때 MonthViewActivity를 띄운다.
                Intent intent = new Intent(getApplicationContext(),
                        MonthViewActivity.class);
                //이전 버튼을 누를시 바뀐 년, 월을 보내줌.
                intent.putExtra("year", iYear);
                intent.putExtra("month", iMonth);
                startActivity(intent); //위에서 설정해 놓은 인텐트 시작하기.
                finish(); //끝내기
            }
        });

        //다음 버튼
        btn = (Button)findViewById(R.id.next);
        //다음 버튼을 누를시 이벤트 발생
        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (iMonth==11) { //12월달에서 다음 버튼을 눌렀을 경우 1월달로 이동
                    iMonth = 0;
                    iYear++;
                }
                else //12월달이 아닌 달은 1씩 늘리기
                    iMonth++;

                //새로운 인텐트를 띄울 때 MonthViewActivity를 띄운다.
                Intent intent = new Intent(getApplicationContext(),
                        MonthViewActivity.class);
                //다음 버튼을 누를시 바뀐 년, 월을 보내줌.
                intent.putExtra("year", iYear);
                intent.putExtra("month", iMonth);
                startActivity(intent); //위에서 설정해 놓은 인텐트 시작하기.
                finish(); //끝내기
            }

        });

        //그리드뷰에 그리드(일자)를 누를시 이벤트 발생
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //position은 내가 누른 위치.
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // 첫 날 이상 마지막 날 이하일 경우 토스트메시지 발생.
                if ((0 < position-firstDay+1) && (position-firstDay+1)<(firstDay+totDays))
                    // ex) 2021/4/21
                    Toast.makeText(MonthViewActivity.this, (iYear)+"/"+(iMonth+1)+"/"+(position-firstDay+1), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //그리드뷰의 내용을 채우기 위한 그리드 어댑터 생성. 그리드 어댑터 클래스.
    private class GridAdapter extends BaseAdapter{

        // dayList
        private final List<String> list;
        // LayoutInflater 객체 생성.
        private final LayoutInflater inflater;

        // 생성자
        public GridAdapter(Context context, List<String> list) {
            this.list = list; // 매개변수로 입력 받은 dayList로 list객체 초기화.
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //context에서 LayoutInflater 가져옴.
        }

        @Override
        public int getCount() {
            return list.size();
        } // 리스트 크기 반환

        @Override
        public String getItem(int position) {
            return list.get(position);
        } //리스트 해당 위치의 일자 반환.

        @Override
        public long getItemId(int position) {
            return position;
        } // 위치 반환(몇번째 블록인지)

        public View getView(int position, View convertView, ViewGroup parent) { // position번째(블락마다 0번째, 1번째 ...)의 블락에 대한 그 하나의 converView 정의

            ViewHolder holder = null; //ViewHolder 객체 저장.

            if (convertView == null) { // convertView는 그리드의 한 블락(하나의 뷰)
                convertView = inflater.inflate(R.layout.calendar_gridview, parent, false); // calendar_gridview.xml파일을 View객체로 만들어서 반환.
                holder = new ViewHolder(); // View를 담고 있는 ViewHolder 객체 생성. convertView(블락 전체)>holder(TextView를 갖고 있는 클래스),각 블락마다 홀더를 갖고 있음>TextView
                //holder는 블락 안의 일자들 모두를 말함. 블락 모두는 convertView임.
                holder.tvItem = (TextView) convertView.findViewById(R.id.tv_item_gridview); // holder의 TextView객체에 calendar_gridview.xml의 tv_item_gridview(TextView)연결.
                convertView.setTag(holder); // convertView(블락마다)의 태그가 있는데 둘이 연결시키기 위해 그 태그를 holder로 설정.
            } else {
                holder = (ViewHolder) convertView.getTag(); //이미 있는 convertView의 태그를 가져와서 holder객체 초기화. holder는 convertView의 Tag.
            } // 내이셔널 지오그래피 옷: 컨버트뷰, 옷에 달려있는 간지러운 애(라벨): 홀더 즉, 태그, 라벨에 적힌 내이셔널 지오그래피 글자: TextView, 옷장: 그리드뷰

            holder.tvItem.setText(getItem(position)); // 홀더 즉,태그에 글자를 써라--> dayList에 있는 일자 정보 적기.
            int color = Color.BLACK; // 평일은 검정
            switch (position % 7) {
                case 0: // 일요일은 빨강
                    color = Color.RED;
                    break;
                case 6: // 토요일은 파랑
                    color = Color.BLUE;
                    break;
            }

            holder.tvItem.setTextColor(color); // 글자색 설정.
            return convertView; // 뷰 생성해라.



        }
    }
    // ViewHolder 클래스 생성. 태그에 쓰일 클래스 생성
    private class ViewHolder {
        TextView tvItem; // 텍스트뷰
    }

    // 입력 받은 연도와 월을 바탕으로 dayList 설정.(데이터 채워 넣기)
    private void setCalendar(int year, int month){

        //첫날이 무슨요일인지, 마지막이 30,31일인지
        Calendar calendar = Calendar.getInstance(); //calendar 객체 생성.
        calendar.set(Calendar.YEAR, year); // 입력 받은 연도로 년 설정.
        calendar.set(Calendar.MONTH, month); // 입력 받은 달로 월 설정.
        calendar.set(Calendar.DAY_OF_MONTH, 1); // firstDay를 만들기 위해 입력받은 년, 월에 대한 일자를 1로 설정한다.
        firstDay = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 첫날이 무슨 요일인지. DAY_OF_WEEK는 일:1, 월:2, 화:3, ..., 토:7인데 convertView나 dayList 등이 0부터 시작하기 떄문에 -1을 해준다.
        totDays = calendar.getActualMaximum(Calendar.DATE); // 마지막날은 그 달의 최대값 반환.

        //이 두 정보를 갖고 리스트 만드려고 한다.
        for (int i=0; i<6*7; i++) { // 42개의 데이터 dayList설정.
            if ( i < firstDay || i > (totDays + firstDay - 1)) dayList.add(""); // 첫날보다 작거나, 마지막날보다 크면 공백으로 채운다.
            else dayList.add("" + (i - firstDay + 1)); // 그게 아니면 해당하는 날짜에 일자를 입력. 1~마지막날까지를 말함.
        }

    }
}