package challenge.models.hackerNews

import challenge.models.hackerNews.User.Id

import play.api.libs.json.{ JsValue, Json, Writes }

case class StoriesWithUsersAndComments(stories: Seq[StoryWithUsersAndComments]) {
  def users: Seq[User] =
    stories.flatMap(_.users)

  def withUpdatedUserTotalCommentCount(usersCommentCount: Map[Id, Int]): StoriesWithUsersAndComments =
    copy(stories = stories.map(_.withUpdatedUserTotalCommentCount(usersCommentCount)))
}

object StoriesWithUsersAndComments {
  trait CommentCountType

  case object TotalCommentCount extends CommentCountType

  case object StoryCommentCount extends CommentCountType

  implicit val writes: Writes[StoriesWithUsersAndComments] =
    (o: StoriesWithUsersAndComments) =>
      Json.obj(
        "stories" -> o.stories
      )
}