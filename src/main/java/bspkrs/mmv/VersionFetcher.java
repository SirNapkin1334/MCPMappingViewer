/*
 * Copyright (C) 2015 bspkrs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package bspkrs.mmv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class VersionFetcher
{
    private static final String jsonUrl = "http://export.mcpbot.bspk.rs/versions.json";
    private final List<String> versions = new ArrayList<>();
    /** Reload if snapshots were downloaded and now aren't, or vice-versa */
    private boolean hasSnapshots = false;

    @SuppressWarnings("unchecked")
    public List<String> getVersions(final boolean snapshots) throws IOException
    {
        if (snapshots != hasSnapshots || versions.isEmpty())
        {
            hasSnapshots = snapshots;
            final URLConnection connection = new URL(jsonUrl).openConnection();
            connection.addRequestProperty("User-Agent", "MMV/1.0.0");
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            Map<String, Object> json = new Gson().fromJson(br, Map.class);

            versions.clear();
            for (String mcVer : json.keySet())
                for (String channel : ((Map<String, ArrayList<Double>[]>) json.get(mcVer)).keySet())
                    if (snapshots || "stable".equals(channel))
                        for (Double ver : ((Map<String, ArrayList<Double>>) json.get(mcVer)).get(channel))
                            versions.add(mcVer + "_" + channel + "_" + String.format("%.0f", ver));
            versions.sort(Collections.reverseOrder(new SplittedNaturalComparator("_")));
        }
        return versions;
    }
}
