package com.natusvincere.nearchat;

import com.natusvincere.nearchat.api.APIClient;
import com.natusvincere.nearchat.api.NearChatUser;

import java.util.ArrayList;
import java.util.List;

public class DataStore
{
    public static APIClient apiClient;
    public static List<NearChatUser> nearbyUsers = new ArrayList<>();
    public static double searchDistance = 20.0; // km
}
