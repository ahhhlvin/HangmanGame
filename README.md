# HangmanGame Android App (LinkedIn REACH Project)

##Instructions for running the HangmanGame Android app:

</u>Running with zipped project file through Android Studio on computer:</u>
From the email, please drag the zipped project file named ‘HangmanGame’ to your desktop and unzip it to reveal all the project files.
To run my Hangman Android app, you must have AndroidStudio installed on your computer. From there, click “import project” and select the unzipped project folder from the desktop —> select “import from existing model — gradel”.
At this point, if you have an Android device you would like to test on, please connect it via USB to the laptop and confirm that it is being recognized by the computer, otherwise continue on to learn how to setup a virtual device on the laptop to run it.
Once Android Studio has been setup with all the project files and assets, please locate the menu bar at the top of your mac, and select the dropdown for “Run” —> select the green play button option that says Run ‘app’.
If you have connected an Android device to the laptop to run the app at this point, a dialog should pop up displaying any available Android devices that can run the app. 
	- Please select the name of your device OR
	- If you don’t have a physical Android device to run on, then select the “Create New Virtual Device” button at the bottom of the dialog box —> select “Nexus 6P” —> click “Next” at the bottom right —> click “Next at the bottom right —> click “Finish” at the bottom right —> you will be brought back to the same “Select Deployment Target” dialog where now you should see the “Nexus 6P” listed under “Available Virtual Devices” —> select that device name
Click the “OK” button at the bottom right of the dialog — and viola the app should now be either running on your physical Android device that is connected or a virtual one on the computer! 

<u>Running directly on Android device using APK:</u>
Open the email on the Android device —> click the .apk file attachment from the email —> select install and follow prompts —> app should start! 


##Project code structure
I designed my Hangman app using the MVP design pattern, which utilizes a clean separation of a Model, View, and Presenter class. The Model class (HangmanModel) represents an instance of the hangman game including all necessary properties. The View class (HangmanActivity) represents all the widgets that are displayed to the user as part of the game interface, which will receive input events. The Presenter class (HangmanPresenter) represents all the business logic that goes on behind the scenes for the game to function. I chose to use this design pattern because it utilizes an organized format for the code that also provides the benefit of easy unit testing since all the logic that needs to be tested exists within the Presenter class (as opposed to randomly sprinkled throughout a large file). I had the time to implement a few unit tests which can be found in the PresenterUnitTests class within the “linkedin.app.hangmangame (test)” located under the “java” folder . My app also implements interfaces which help abstract out the communication between the view that the user interacts with and the presenter that performs the necessary logic based on the user’s actions. This proves beneficial in allowing for easier code refactoring and modifications in the future, without requiring an engineer to scour through every line of the project to make even the simplest change. Lastly, I chose to implement a material design inspired feel to the app with my favorite color (blue!) to give the app a more native Android look that is personalized. 


##Project features and extensions
My HangmanGame app includes the following features/extensions:
- ability to select the difficulty of words the user can guess 
- displaying the appearance of hangman’s body parts upon incorrect guess submissions
- freedom to guess both individual characters of entire words


##Game instructions
- Once the app is opened, a new word of random difficulty will be retrieved and ready for the user to guess after the loading bar has disappeared.
- The user may select a specific word difficulty if they have a preference, otherwise, the default difficulty will be random on each game
- Within one game, the user has 6 attempts to guess either individual letters the word could contain or the entire word itself, however, with every incorrect guess a body part of the hangman will be revealed.
- If all 6 attempts have been used and the entire hangman is revealed without the full word being guessed, the user has lost the game. =(
- If the full word is guessed without using all 6 attempts then the user has won the game! =)
- The user can then start a new round by either selecting another difficulty level or pressing the refresh button at the top right corner.
