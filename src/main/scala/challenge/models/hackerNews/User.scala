package challenge.models.hackerNews

import challenge.models.hackerNews.User.Id

import play.api.libs.json._

case class User(id: Id, storyCommentsCount: Int = 0, totalCommentsCount: Int = 0)

object User {
  type Id = String

  implicit val writes: Writes[User] =
    (o: User) =>
      Json.obj(
        "id" -> o.id,
        "storyCommentsCount" -> o.storyCommentsCount,
        "totalCommentsCount" -> o.totalCommentsCount
      )
}