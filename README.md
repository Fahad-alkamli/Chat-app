# Chat-app
Chat app using android app and google cloud restful API
This project is implemented using Google Resentful API, google storage and an android app.
Please make sure you read the pdf file for help regarding implementing the resentful API and the app.
I also added a video demo showing an interaction between two friends using the app.
Problems Encounter:
1-	Since I am using an endpoint API to fetch new messages I had to reduce the number of Http requests because android will crash my app when I set the interval to low number. 
2-	For uploading a profile picture, I had to add my own google storage key in the app which is not secure and not recommended but that was the only solution I could of implemented since posting to google Resentful API failed badly due to the fact that I didn’t have any control over the post data size and the max_input_time. 
