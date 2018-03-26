package challenge.models.hackerNews

import challenge.models.hackerNews.StoriesWithUsersAndComments.{ CommentCountType, StoryCommentCount, TotalCommentCount }
import challenge.models.hackerNews.User.Id
import challenge.models.responses.hackerNews.{ CommentResponse, StoryResponse }

import play.api.libs.json.{ Json, Writes }

case class StoryWithUsersAndComments(story: StoryResponse, comments: Seq[CommentResponse]) {
  def users: Seq[User] =
    comments.map(_.user)

  def withUpdatedUserStoryCommentCount(usersCommentCount: Map[Id, Int], optNumComments: Option[Int] = None): StoryWithUsersAndComments =
    withUpdatedUserCommentCount(usersCommentCount, StoryCommentCount, optNumComments)

  def withUpdatedUserTotalCommentCount(usersCommentCount: Map[Id, Int]): StoryWithUsersAndComments =
    withUpdatedUserCommentCount(usersCommentCount, TotalCommentCount)

  def withUpdatedUserCommentCount(usersCommentCount: Map[Id, Int], commentCountType: CommentCountType, optNumComments: Option[Int] = None): StoryWithUsersAndComments = {
    def updateUserCommentCount(comment: CommentResponse, fn: Int => CommentResponse): CommentResponse =
      usersCommentCount.get(comment.user.id).fold(comment)(usersCommentCount => fn(usersCommentCount))

    val updatedComments =
      commentCountType match {
        case StoryCommentCount =>
          val updatedComments =
            comments
              .map { comment =>
                updateUserCommentCount(comment, count => comment.copy(user = comment.user.copy(storyCommentsCount = count)))
              }
              .groupBy(_.user.id)
              .map(_._2.head)
              .toSeq
              .sortWith {
                _.user.storyCommentsCount > _.user.storyCommentsCount
              }

          optNumComments.fold(updatedComments) { numComments =>
            updatedComments
              .take(numComments)
          }

        case TotalCommentCount =>
          comments
            .map { comment =>
              updateUserCommentCount(comment, count => comment.copy(user = comment.user.copy(totalCommentsCount = count)))
            }
      }

    copy(comments = updatedComments)
  }
}

object StoryWithUsersAndComments {
  implicit val writes: Writes[StoryWithUsersAndComments] =
    (o: StoryWithUsersAndComments) =>
      Json.obj(
        "story" -> o.story,
        "comments" -> o.comments
      )
}

