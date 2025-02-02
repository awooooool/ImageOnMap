/*
 * Copyright or © or Copr. Moribus (2013)
 * Copyright or © or Copr. ProkopyL <prokopylmc@gmail.com> (2015)
 * Copyright or © or Copr. Amaury Carrade <amaury@carrade.eu> (2016 – 2021)
 * Copyright or © or Copr. Vlammar <valentin.jabre@gmail.com> (2019 – 2021)
 *
 * This software is a computer program whose purpose is to allow insertion of
 * custom images in a Minecraft world.
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package fr.moribus.imageonmap.commands.maptool;

import fr.moribus.imageonmap.Permissions;
import fr.moribus.imageonmap.commands.IoMCommand;
import fr.moribus.imageonmap.i18n.I;
import fr.moribus.imageonmap.ui.MapItemManager;
import fr.moribus.imageonmap.commands.CommandException;
import fr.moribus.imageonmap.commands.CommandInfo;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(name = "getremaining", aliases = {"getrest"})
public class GetRemainingCommand extends IoMCommand {
    @Override
    protected void run() throws CommandException {
        Player player = playerSender();

        if (MapItemManager.getCacheSize(player) <= 0) {
            info(I.t("You have no remaining map."));
            return;
        }

        int givenMaps = MapItemManager.giveCache(player);

        if (givenMaps == 0) {
            error(I.t("Your inventory is full! Make some space before requesting the remaining maps."));
        } else {
            info(I.tn("There is {0} map remaining.", "There are {0} maps remaining.",
                    MapItemManager.getCacheSize(player)));
        }
    }

    @Override
    public boolean canExecute(CommandSender sender) {
        return Permissions.NEW.grantedTo(sender) || Permissions.GET.grantedTo(sender);
    }
}
