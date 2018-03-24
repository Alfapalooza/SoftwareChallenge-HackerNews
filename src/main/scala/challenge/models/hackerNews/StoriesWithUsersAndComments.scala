package challenge.models.hackerNews

import challenge.models.responses.hackerNews.StoryResponse

case class StoriesWithUsersAndComments(storyResponse: StoryResponse, comments: Seq[])
