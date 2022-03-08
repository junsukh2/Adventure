package student.adventure;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

public class Layout {
    private String startingRoom;    // the String name of the room the player starts in
    private String endingRoom;    // the String name of the room the player must reach to win
    private Room[] rooms;
    private String videoUrl;

    @JsonCreator
    public Layout (
            @JsonProperty ("startingRoom") String startingRoom,
            @JsonProperty ("endingRoom") String endingRoom,
            @JsonProperty ("videoUrl") String videoUrl,
            @JsonProperty ("rooms") Room[] rooms) {
        this.startingRoom = startingRoom;
        this.endingRoom = endingRoom;
        this.videoUrl = videoUrl;
        this.rooms = rooms;
    }

    public String getStartingRoom() {
        return startingRoom;
    }

    public String getEndingRoom() {
        return endingRoom;
    }

    public Room[] getRooms() {
        return rooms;
    }

    public void setStartingRoom(String startingRoom) {
        this.startingRoom = startingRoom;
    }

    public void setEndingRoom(String endingRoom) {
        this.endingRoom = endingRoom;
    }

    public void setRooms(Room[] rooms) {
        this.rooms = rooms;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public static class Room {
        private String name;
        private String description;
        private Direction[] directions;
        private String[] items;
        private String image;

        @JsonCreator
        public Room (@JsonProperty ("name") String name,
                     @JsonProperty ("description") String description,
                     @JsonProperty ("directions") Direction[] directions,
                     @JsonProperty ("items") String[] items,
                     @JsonProperty ("image") String image) {
            this.name = name;
            this.description = description;
            this.directions = directions;
            this.items = items;
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Direction[] getDirections() {
            return directions;
        }

        public String[] getItems() {
            return items;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setDirections(Direction[] directions) {
            this.directions = directions;
        }

        public void setItems(String[] items) {
            this.items = items;
        }

        public String getImage() {
            return image;
        }

        public static class Direction {
            String directionName;
            String room;    // the String name of the room this direction points to

            @JsonCreator
            public Direction (@JsonProperty ("directionName") String directionName,
                              @JsonProperty ("room") String room) {
                this.directionName = directionName;
                this.room = room;
            }

            public String getDirectionName() {
                return directionName;
            }

            public String getRoom() {
                return room;
            }

            public void setDirectionName(String directionName) {
                this.directionName = directionName;
            }

            public void setRoom(String room) {
                this.room = room;
            }
        }//Direction
    }//Room
}//Layout
