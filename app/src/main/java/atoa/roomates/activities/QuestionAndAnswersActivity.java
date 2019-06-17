package atoa.roomates.activities;
/**
 * Copyright 2016 Avi twito,Or Am-Amshalem
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.ArrayList;

import atoa.roomates.adapters.QuestionAndAnswersAdapter;
import atoa.roomates.models.QuestionAndAnswer;
import atoa.roomates.R;
import atoa.roomates.support.GenericMethods;

/**
 * class to represent the common questions and answers screen
 */
public class QuestionAndAnswersActivity extends AppCompatActivity {
    RecyclerView mQuestionList;
    ArrayList<QuestionAndAnswer> mList;
    QuestionAndAnswersAdapter mAdapter;
    String[] mQuestions;
    String[] mAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_and_answers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.questions_and_answers));


        mQuestions = getResources().getStringArray(R.array.questions);
        mAnswers = getResources().getStringArray(R.array.answers);
        mList = new ArrayList<>();
        for (int i = 0 ; i < mQuestions.length ; i++){
            QuestionAndAnswer q = new QuestionAndAnswer(mQuestions[i], mAnswers[i]);
            mList.add(q);
        }


        mAdapter = new QuestionAndAnswersAdapter(mList,this);
        mQuestionList = (RecyclerView)findViewById(R.id.question_and_answers_list);
        GenericMethods.getInstance(this).setRecyclerSetting(mQuestionList);
        mQuestionList.setAdapter(mAdapter);
    }
}
