package apis.ombi;


import apis.ombi.templateClasses.movies.moreInfo.MovieInfo;
import apis.ombi.templateClasses.movies.search.MovieSearch;
import apis.ombi.templateClasses.requests.movie.MovieRequest;
import apis.ombi.templateClasses.requests.tv.jsonTemplate.TvRequestTemplate;
import apis.ombi.templateClasses.tv.moreInfo.TvInfo;
import apis.ombi.templateClasses.tv.search.TvSearch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import discordBot.EmbedManager;
import net.dv8tion.jda.api.EmbedBuilder;
import okhttp3.*;
import settingsManager.Settings;

import java.io.IOException;
import java.util.ArrayList;

public class OmbiCallers {


    //This method returns an Object array that contains the value returned by the Ombi Api
    public TvSearch[] ombiTvSearch(String toSearch) {

        //Create new objects
        OkHttpClient client = new OkHttpClient();
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        //Create the request
        Request request = new Request.Builder()
                .url(Settings.getOmbiUrl() + "/api/v1/Search/tv/" + formatSearchTerm(toSearch))
                .addHeader("accept", "application/json")
                .addHeader("ApiKey", Settings.getOmbiKey())
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            //Download the Json returned by the API and make it into a string
            String downloadedJson = response.body().string();
            System.out.println(downloadedJson);
            //Pass the String to Gson and have it turned into a TvSearch Array
            TvSearch[] result = gson.fromJson(downloadedJson, TvSearch[].class);
            //Log the number of items in the array
            System.out.println("The array is " + result.length + " items long");
            //return the array
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("error");
        return null;
    }

    public MovieSearch[] ombiMovieSearch(String toSearch) {
        //Create new objects
        OkHttpClient client = new OkHttpClient();
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        //Create the request
        Request request = new Request.Builder()
                .url(Settings.getOmbiUrl() + "/api/v1/Search/movie/" + formatSearchTerm(toSearch))
                .addHeader("accept", "application/json")
                .addHeader("ApiKey", Settings.getOmbiKey())
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            //Download the Json returned by the API and make it into a string
            String downloadedJson = response.body().string();
            System.out.println(downloadedJson);
            //Pass the String to Gson and have it turned into a TvSearch Array
            MovieSearch[] result = gson.fromJson(downloadedJson, MovieSearch[].class);
            //Log the number of items in the array
            System.out.println("The array is " + result.length + " items long");
            //return the array
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("error");
        return null;
    }

    //Methods to get the JSON for the more info endpoint of the Ombi API
    public TvInfo ombiTvInfo(String id) {

        OkHttpClient client = new OkHttpClient();
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        //create the request
        Request request = new Request.Builder()
                .url(Settings.getOmbiUrl() + "/api/v1/search/tv/info/" + id)
                .addHeader("accept", "application/json")
                .addHeader("ApiKey", Settings.getOmbiKey())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected Code: " + response);
            }

            String downloadedJSON = response.body().string();
            System.out.println(downloadedJSON);

            return gson.fromJson(downloadedJSON, TvInfo.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public TvInfo[] getTvInfoArray(TvSearch[] searchArray) {
        TvInfo[] infoArray = new TvInfo[searchArray.length];

        for (int i = 0; i < searchArray.length; i++) {
            infoArray[i] = ombiTvInfo(String.valueOf(searchArray[i].getId()));
        }
        return infoArray;
    }

    public MovieInfo ombiMovieInfo(String id) {
        System.out.println("This was called");
        OkHttpClient client = new OkHttpClient();
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        //create the request
        Request request = new Request.Builder()
                .url(Settings.getOmbiUrl() + "/api/v1/search/movie/info/" + id)
                .addHeader("accept", "application/json")
                .addHeader("ApiKey", Settings.getOmbiKey())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected Code: " + response);
            }

            String downloadedJSON = response.body().string();
            System.out.println(downloadedJSON);
            return gson.fromJson(downloadedJSON, MovieInfo.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public MovieInfo[] getTvInfoArray(MovieInfo[] searchArray) {
        MovieInfo[] infoArray = new MovieInfo[searchArray.length];

        for (int i = 0; i < searchArray.length; i++) {
            infoArray[i] = ombiMovieInfo(String.valueOf(searchArray[i].getId()));
        }
        return infoArray;
    }

    public String requestMovie(String id) {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();


        RequestBody requestBody = RequestBody.create("{\"theMovieDbId\": " + id + ",\"languageCode\":\"string\"}", MediaType.parse("text"));
        Request request = new Request.Builder()
                .url(Settings.getOmbiUrl() + "/api/v1/request/movie")
                .post(requestBody)
                .addHeader("Accept", "application/json")
                .addHeader("ApiKey", Settings.getOmbiKey())
                .addHeader("Content-Type", "text/json")
                .build();

        try {
            Response response = client.newCall(request).execute();

            String downloadedJSON = response.body().string();
            System.out.println("The JSON is " + downloadedJSON);

            MovieRequest requestObj = gson.fromJson(downloadedJSON, MovieRequest.class);

            if (requestObj.getIsError()) {
                return requestObj.getErrorMessage().toString();
            } else {
                return requestObj.getMessage();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unable to request media";
    }

    public String requestTv(String id) {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();


        RequestBody requestBody = RequestBody.create("{\"theMovieDbId\": " + id + ",\"languageCode\":\"string\"}", MediaType.parse("text"));
        Request request = new Request.Builder()
                .url(Settings.getOmbiUrl() + "/api/v1/request/tv")
                .post(requestBody)
                .addHeader("Accept", "application/json")
                .addHeader("ApiKey", Settings.getOmbiKey())
                .addHeader("Content-Type", "text/json")
                .build();

        try {
            Response response = client.newCall(request).execute();

            String downloadedJSON = response.body().string();
            System.out.println("The JSON is " + downloadedJSON);

            MovieRequest requestObj = gson.fromJson(downloadedJSON, MovieRequest.class);

            if (requestObj.getIsError()) {
                return requestObj.getErrorMessage().toString();
            } else {
                return requestObj.getMessage();
            }


        } catch (Exception e) {
            System.out.println(e);
            return "Unable to add media to request list";
        }

    }

    public EmbedBuilder getMissingEpisodeEmbed(String id) {

        System.out.println("TEST");
        //get the TvInfo Object
        TvInfo tvInfo = ombiTvInfo(id);
        EmbedManager eb = new EmbedManager();

        //get the TvRequest Template object that contains the missing episodes
        TvRequestTemplate missingEpisodes = tvInfo.getMissingEpisodeArray();

        ArrayList<String> missingEpisodeslist = new ArrayList<>();


        for (int i = 0; i < missingEpisodes.getSeasons().size(); i++) {
            for (int j = 0; j < missingEpisodes.getSeasons().get(i).getRequestEpisodes().size(); j++) {
                missingEpisodeslist.add("Missing S" + (i + 1) + "E" + missingEpisodes.getSeasons().get(i).getRequestEpisodes().get(j).getEpisodeNumber());
            }
        }

        return eb.createMissingEpisodeEmbed(missingEpisodeslist);


    }
    //Helper methods
    private String formatSearchTerm(String query) {
        //format searchQuery
        return query.toLowerCase().replaceAll(" ", "%20");
    }
}
