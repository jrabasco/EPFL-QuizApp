package epfl.sweng.showquestions;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;
import epfl.sweng.QuizQuestion;
import epfl.sweng.R;
import epfl.sweng.servercomm.ServerCommunicator;
import epfl.sweng.testing.TestingTransactions;
import epfl.sweng.testing.TestingTransactions.TTChecks;
import epfl.sweng.ui.QuestionActivity;

/**
 * Activity to download a question and display it
 */
public class ShowQuestionsActivity extends QuestionActivity {

	private final static int PADDING_RIGHT = 23;
	private final static int PADDING = 0;

	private ShowQuestionsActivity mSelf;
	private QuizQuestion mRandomQuestion;
	private Button mNextQuestion;
	private TextView mCorrectness;
	private LinearLayout mLinearLayout;
	private ListView mAnswersList;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ServerCommunicator.getInstance().addObserver(this);
		mSelf = this;
		getQuestion();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_questions, menu);
		return true;
	}

	/**
	 * Downloads and displays a new random question
	 */
	public void getQuestion() {
		// creates the main layout
		mLinearLayout = new LinearLayout(this);
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);
		mLinearLayout.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		// downloads a random question from the server
		ServerCommunicator.getInstance().getRandomQuestion();
		// show a progress dialog while waiting for question
		showProgressDialog();
	}

	@Override
	protected boolean mustTakeAccountOfUpdate() {
		return ServerCommunicator.getInstance().isFetchingQuestion();
	}

	@Override
	protected void processDownloadedData(Object data) {
		mRandomQuestion = (QuizQuestion) data;
		showQuestion();
	}

	public void showQuestion() {
		// Display the text of the question
		TextView question = new TextView(this);
		question.setText(mRandomQuestion.getQuestion());
		mLinearLayout.addView(question);

		// display the answers
		displayAnswers();
		
		mCorrectness = new TextView(this);
		mCorrectness.setText("Wait for an answer...");
		mCorrectness.setPadding(10, 20, 0, 0);
		mLinearLayout.addView(mCorrectness);
		
		// initializes the button nextQuestion
		mNextQuestion = new Button(this);
		mNextQuestion.setText(R.string.next_question);
		mNextQuestion.setEnabled(false);
		mNextQuestion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getQuestion();
			}
		});
		mLinearLayout.addView(mNextQuestion);

		// Display the tags
		displayTags();

		// Debug: Display the solution 
		//displaySolutionIndex();
		 

		setContentView(mLinearLayout);
		TestingTransactions.check(TTChecks.QUESTION_SHOWN);
	}

	public void displayAnswers() {
		// Initialize the arrays of answers and correctness sign
		int totalAnswer = mRandomQuestion.getAnswers().length;
		

		mAnswersList = new ListView(this);
		mAnswersList.setPadding(10, 20, 0, 0);
		
		mAnswersList.setOnItemClickListener(new AnswerOnClickListener());
		mAnswersList.setAdapter(new AnswerListAdapter());
		
		mLinearLayout.addView(mAnswersList);
	}


	public void displayTags() {
		int totalTags = mRandomQuestion.getTags().length;
		LinearLayout tagLayout = new LinearLayout(this);
		tagLayout.setOrientation(LinearLayout.HORIZONTAL);
		tagLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		for (int i = 0; i < totalTags; i++) {
			TextView tagText = new TextView(this);
			tagText.setText(mRandomQuestion.getTags()[i]);
			tagText.setPadding(PADDING, PADDING, PADDING_RIGHT, PADDING);
			tagText.setTextColor(Color.GRAY);
			tagLayout.addView(tagText);
		}
		mLinearLayout.addView(tagLayout);

	}

	public void displaySolutionIndex() {
		TextView solutionIndex = new TextView(this);
		int index = mRandomQuestion.getSolutionIndex();
		String solutionText = String.valueOf(index);
		solutionIndex.setText(solutionText);
		mLinearLayout.addView(solutionIndex);
	}
	
	/**
	 * Private class for handling click on answer buttons
	 */
	private final class AnswerOnClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> listView, View view, int position,
				long id) {
			
			if (mRandomQuestion.isSolution(position)) {
				mCorrectness.setText(getString(R.string.button_check));
				mNextQuestion.setEnabled(true);
				mAnswersList.setEnabled(false);
			} else {
				mCorrectness.setText(getString(R.string.button_cross));
			}
			
			TestingTransactions.check(TTChecks.ANSWER_SELECTED);
		}
	}

	private class AnswerListAdapter extends ArrayAdapter<String> {

		public AnswerListAdapter() {
			super(mSelf, 0, mRandomQuestion.getAnswers());
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView answerText = new TextView(mSelf);
			answerText.setPadding(5, 20, 0, 20);
			answerText.setText(mRandomQuestion.getAnswers()[position]);
			return answerText;
		}
	
	}
}
